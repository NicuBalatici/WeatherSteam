from django.contrib.auth import authenticate
from django.contrib.auth.models import User
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import json
from django.contrib.auth.models import User
from django.db import IntegrityError


@csrf_exempt
def login_api(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            email = data.get('email')
            password = data.get('password')

            # Find user by email
            try:
                user_obj = User.objects.get(email=email)
                username = user_obj.username
            except User.DoesNotExist:
                # CHANGED: Removed status=404
                return JsonResponse({'success': False, 'message': 'Email not found'})

                # Check password
            user = authenticate(username=username, password=password)

            if user is not None:
                return JsonResponse({
                    'success': True,
                    'message': 'Login Successful',
                    'token': 'demo-token',  # Added dummy token for safety
                    'user_id': user.id,
                    'username': user.username
                })
            else:
                # CHANGED: Removed status=401
                return JsonResponse({'success': False, 'message': 'Invalid Password'})

        except Exception as e:
            return JsonResponse({'success': False, 'message': str(e)}, status=400)

    return JsonResponse({'message': 'POST required'}, status=400)


@csrf_exempt
def register_api(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            email = data.get('email')
            password = data.get('password')
            username = data.get('username')

            # 1. Check if Email is taken
            if User.objects.filter(email=email).exists():
                return JsonResponse({'success': False, 'message': 'Email already registered'})

            # 2. NEW: Check if Username is taken
            if User.objects.filter(username=username).exists():
                return JsonResponse({'success': False, 'message': 'Username already taken. Try another.'})

            # 3. Create User
            user = User.objects.create_user(username=username, email=email, password=password)
            user.save()
            return JsonResponse({'success': True, 'message': 'Account created successfully!'})

        except Exception as e:
            print(f"Server Error: {e}")
            return JsonResponse({'success': False, 'message': 'Server Error'}, status=400)