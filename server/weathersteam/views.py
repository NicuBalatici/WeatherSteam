import datetime
import uuid

import bcrypt
from django.contrib.auth.hashers import BCryptPasswordHasher
from django.views.decorators.http import require_http_methods
import time
import jwt
from django.http import JsonResponse
import json
from django.conf import settings
import random

from weathersteam.models import Users, Games, UsersGames


@require_http_methods(["GET"])
def api_status():
    """
    Returns a simple JSON response to confirm the API is running and accessible.
    """

    status_data = {
        "status": "OK",
        "service": "WeatherStream Backend API",
        "current_time": time.strftime("%Y-%m-%d %H:%M:%S", time.gmtime()),
        "message": "The API endpoint is active and serving requests.",
    }

    # JsonResponse automatically sets the Content-Type to application/json
    return JsonResponse(status_data, status=200)

@require_http_methods(["POST"])
def api_users():

    hasher = BCryptPasswordHasher()
    hasher.rounds = 13

    new_user = {
        "id": uuid.uuid4(),
        "name": "django",
        "password": hasher.encode("password", ""),
        "steam_id": None,
        "vanity_url_name": None
    }

    print(new_user)

    return JsonResponse({"status": "CREATED"})

@require_http_methods(["POST"])
def api_login(request):
    try:
        data = json.loads(request.body)
        email = data.get('email')
        password = data.get('password')

        user = Users.objects.get(email=email)

        if user is None:
            return JsonResponse({'success': False, 'message': 'Invalid credentials'}, status=401)

        input_bytes = password.encode('utf-8')
        database_bytes = user.password.encode('utf-8')

        if bcrypt.checkpw(input_bytes, database_bytes):
            payload = {
                'user_id': str(user.id),
                'username': user.username,
                'email': user.email,
                'exp': datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(days=7),
                'iat': datetime.datetime.now(datetime.timezone.utc)
            }

            token = jwt.encode(payload, settings.SECRET_KEY, algorithm='HS256')

            return JsonResponse({
                'success': True,
                'username': user.username,
                'token': token
            }, status=200)
        else:
            return JsonResponse({'success': False, 'message': 'Invalid credentials'}, status=401)
    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)

@require_http_methods(["POST"])
def api_register(request):
    try:
        data = json.loads(request.body)
        email = data.get('email')
        password = data.get('password')
        username = data.get('username')

        if Users.objects.filter(email=email).exists():
            return JsonResponse({'success': False, 'message': 'Email already registered'})

        if Users.objects.filter(username=username).exists():
            return JsonResponse({'success': False, 'message': 'Username already exists'})

        bytes_password = password.encode('utf-8')
        hashed_password_bytes = bcrypt.hashpw(bytes_password, bcrypt.gensalt(13))
        hashed_password_string = hashed_password_bytes.decode('utf-8')

        user_id = uuid.uuid4()
        user = Users.objects.create(id=user_id, username=username, email=email, password=hashed_password_string)
        user.save()

        return JsonResponse({'success': True, 'message': 'Account created successfully!'}, status=201)
    except Exception as e:
        print(f"Server Error: {e}")
        return JsonResponse({'success': False, 'message': 'Server Error'}, status=500)

def generate_unique_username(base_username):
    new_username = base_username
    counter = 1
    while Users.objects.filter(username=new_username).exists():
        new_username = f"{base_username}#{random.randint(1000, 9999)}"
        counter += 1
    return new_username

@require_http_methods(["POST"])
def api_game_add(request):
    try:
        data = json.loads(request.body)
        steam_game_id = data.get('steam_game_id')
        title = data.get('title')
        image_url = data.get('image_url')
        tags = data.get('tags')

        if Games.objects.filter(steam_game_id=steam_game_id).exists() or Games.objects.filter(title=title).exists() or Games.objects.filter(image_url=image_url).exists():
            return JsonResponse({'success': False, 'message': 'Game already exists'}, status=200)

        game_id = uuid.uuid4()

        new_game = Games.objects.create(id=game_id, title=title, steam_game_id=steam_game_id, image_url=image_url, tags=tags)
        new_game.save()

        return JsonResponse({'success': True, 'message': 'Game added successfully!'}, status=201)
    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)

@require_http_methods(["GET"])
def api_game_user(request):
    try:
        user_id = uuid.UUID(request.GET.get('user_id'))
        weather = str(request.GET.get('weather'))
        mood = str(request.GET.get('mood'))
        pace = str(request.GET.get('pace'))
        difficulty = str(request.GET.get('difficulty'))

        if user_id is None:
            return JsonResponse({'success': False, 'message': 'User id is None'}, status=400)

        if not Users.objects.filter(id=user_id).exists():
            return JsonResponse({'success': False, 'message': 'User not found'}, status=404)

        game_ids_queryset = UsersGames.objects.filter(user_id=user_id).values('game_id')

        game_ids = list(game_id['game_id'] for game_id in game_ids_queryset)

        games_queryset = Games.objects.filter(id__in=game_ids).values('id', 'steam_game_id', 'title', 'image_url', 'tags').distinct()

        games = list(games_queryset)

        if weather and weather != "" and weather != "None":
            games = [game for game in games if weather == game["tags"].split(',')[0]]

        if mood and mood != "" and mood != "None":
            games = [game for game in games if mood == game["tags"].split(',')[1]]

        if pace and pace != "" and pace != "None":
            games = [game for game in games if pace == game["tags"].split(',')[2]]

        if difficulty and difficulty != "" and difficulty != "None":
            games = [game for game in games if difficulty == game["tags"].split(',')[3]]

        return JsonResponse({'success': True, 'games': games}, status=200)
    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)

@require_http_methods(["POST"])
def api_steam(request):
    try:
        data = json.loads(request.body)
        username = data.get('username')
        steam_id = data.get('steam_id')

        if username is None:
            return JsonResponse({'success': False, 'message': 'Invalid login request'}, status=400)

        user = None

        if steam_id is not None:
            if Users.objects.filter(steam_id=steam_id).exists():
                user = Users.objects.get(steam_id=steam_id)

        if user is None:
            user_id = uuid.uuid4()

            if Users.objects.filter(username=username).exists():
                username = generate_unique_username(username)

            new_user = Users.objects.create(id=user_id, username=username, steam_id=steam_id, vanity_url_name=None, password=None, email=None)
            new_user.save()
            user = new_user

        payload = {
            'user_id': str(user.id),
            'username': user.username,
            'steam_id': user.steam_id,
            'exp': datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(days=7),
            'iat': datetime.datetime.now(datetime.timezone.utc)
        }

        token = jwt.encode(payload, settings.SECRET_KEY, algorithm='HS256')

        return JsonResponse({
            'success': True,
            'username': user.username,
            'token': token
        }, status=200)

    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)