from django.urls import path
from . import views

# Define the app_name for namespacing (good practice)
app_name = 'weathersteam'

urlpatterns = [
    path('status/', views.api_status, name='api_status'),
    path('users/', views.api_users, name='api_users'),
    path('register/', views.api_register, name='api_register'),
    path('login/', views.api_login, name='api_login'),
]