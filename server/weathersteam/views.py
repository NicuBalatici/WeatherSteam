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

from weathersteam.models import Users


@require_http_methods(["GET"])
def api_status(request):
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
    if request.method == 'POST':
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
                })
            else:
                return JsonResponse({'success': False, 'message': 'Invalid credentials'}, status=401)
        except Exception as e:
            return JsonResponse({'success': False, 'message': str(e)}, status=400)

    return None

@require_http_methods(["POST"])
def api_register(request):
    if request.method == 'POST':
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

            return JsonResponse({'success': True, 'message': 'Account created successfully!'})

        except Exception as e:
            print(f"Server Error: {e}")
            return JsonResponse({'success': False, 'message': 'Server Error'}, status=400)

    return None

def generate_unique_username(base_username):
    new_username = base_username
    counter = 1
    while Users.objects.filter(username=new_username).exists():
        new_username = f"{base_username}#{random.randint(1000, 9999)}"
        counter += 1
    return new_username