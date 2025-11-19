import uuid

from django.contrib.auth.hashers import BCryptPasswordHasher
from django.http import JsonResponse
from django.views.decorators.http import require_http_methods
import time


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