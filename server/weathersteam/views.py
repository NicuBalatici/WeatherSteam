import datetime
import uuid

import bcrypt
from django.contrib.auth.hashers import BCryptPasswordHasher
from django.db import connection
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

@require_http_methods(["GET"])
def api_game_steam_id(request):
    try:
         steam_game_id = request.GET.get('steam_game_id')

         if not steam_game_id:
             return JsonResponse({'success': False, 'message': 'Bad request', 'game': None}, status=400)

         game = Games.objects.filter(steam_game_id=steam_game_id).first()

         if game:
             return JsonResponse({'success': True, 'message': 'Game found','game': {
                 'id': str(game.id),  # Convert UUID to string
                 'steam_game_id': game.steam_game_id,
                 'title': game.title,
                 'image_url': game.image_url,
                 'tags': game.tags
             }}, status=200)

         return JsonResponse({'success': True, 'message': 'Game does not exist', 'game': None}, status=200)
    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)
@require_http_methods(["POST"])
def api_game_add(request):
    try:
        data = json.loads(request.body)
        steam_game_id = data.get('steam_game_id')
        title = data.get('title')
        image_url = data.get('image_url')
        tags = data.get('tags')

        existing_game = Games.objects.filter(steam_game_id=steam_game_id).first()
        if existing_game:
            return JsonResponse({'success': True, 'message': 'Game already exists', 'game_id:': str(existing_game.id)}, status=200)

        game_id = uuid.uuid4()

        new_game = Games.objects.create(id=game_id, title=title, steam_game_id=steam_game_id, image_url=image_url, tags=tags)
        new_game.save()

        return JsonResponse({'success': True, 'message': 'Game added successfully!', 'game_id': str(game_id)}, status=201)
    except Exception as e:
        return JsonResponse({'success': False, 'message': str(e)}, status=500)


@require_http_methods(["GET"])
def api_game_user(request):
    try:
        user_id_str = request.GET.get('user_id')
        weather = request.GET.get('weather')
        lighting = request.GET.get('lighting')
        mood = request.GET.get('mood')
        pace = request.GET.get('pace')
        difficulty = request.GET.get('difficulty')

        if not user_id_str:
            return JsonResponse({'success': False, 'message': 'User id is missing'}, status=400)

        user_id = uuid.UUID(user_id_str)
        if not Users.objects.filter(id=user_id).exists():
            return JsonResponse({'success': False, 'message': 'User not found'}, status=404)

        user_game_ids = UsersGames.objects.filter(user_id=user_id).values_list('game_id', flat=True)
        games_query = Games.objects.filter(id__in=user_game_ids)

        if weather and weather != "None":
            weather_map = {
                "CLOUDS": "Strategy",
                "SUN": "Adventure",
                "RAIN": "Puzzle",
                "SNOW": "RPG",
                "CLEAR": "Action",
                "THUNDER": "Indie",
                "MIST": "Simulation"
            }
            target_genre = weather_map.get(weather)
            if target_genre:
                games_query = games_query.filter(tags__icontains=target_genre)

        if lighting and lighting != "UNKNOWN":
            light_map = {
                "DARK": "DARK",
                "DIM": "Strategy",
                "BRIGHT": "LIGHT"
            }

            target_light_tag = light_map.get(lighting)

            if target_light_tag:
                games_query = games_query.filter(tags__icontains=target_light_tag)

        if mood and mood not in ["None", ""]:
            games_query = games_query.filter(tags__icontains=mood)

        if pace and pace not in ["None", ""]:
            games_query = games_query.filter(tags__icontains=pace)

        if difficulty and difficulty not in ["None", ""]:
            games_query = games_query.filter(tags__icontains=difficulty)

        games_data = list(games_query.values('id', 'steam_game_id', 'title', 'image_url', 'tags'))

        if not games_data:
            fallback_query = Games.objects.filter(id__in=user_game_ids).order_by('?')[:1]
            games_data = list(fallback_query.values('id', 'steam_game_id', 'title', 'image_url', 'tags'))

        return JsonResponse({'success': True, 'games': games_data}, status=200)

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

@require_http_methods(["POST"])
def api_game_user_add(request):
    try:
        data = json.loads(request.body)

        user_id = uuid.UUID(data['user_id'])
        game_id = uuid.UUID(data['game_id'])

        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT 1 FROM users_games WHERE user_id = %s AND game_id = %s",
                [user_id, game_id]
            )
            exists = cursor.fetchone()

            if exists:
                return JsonResponse({'success': True, 'message': 'User already has this game'}, status=200)

            # 2. Insert new link (Raw SQL)
            cursor.execute(
                "INSERT INTO users_games (user_id, game_id) VALUES (%s, %s)",
                [user_id, game_id]
            )

        return JsonResponse({'success': True, 'message': 'Game linked successfully'}, status=200)

    except Exception as e:
        print(f"SERVER ERROR: {str(e)}")
        return JsonResponse({'success': False, 'message': str(e)}, status=500)
