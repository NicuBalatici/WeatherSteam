from django.db import models

# Create your models here.
class Games(models.Model):
    steam_game_id = models.BigIntegerField(unique=True)
    id = models.UUIDField(primary_key=True)
    image_url = models.CharField(unique=True, max_length=255, blank=True, null=True)
    tags = models.CharField(max_length=255, blank=True, null=True)
    title = models.CharField(unique=True, max_length=255)

    class Meta:
        managed = False
        db_table = 'games'

class Users(models.Model):
    steam_id = models.BigIntegerField(unique=True, blank=True, null=True)
    id = models.UUIDField(primary_key=True)
    username = models.CharField(unique=True, max_length=255)
    password = models.CharField(max_length=255, null=True)
    vanity_url_name = models.CharField(unique=True, max_length=255, blank=True, null=True)
    email = models.CharField(unique=True, max_length=255, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'users'

class UsersGames(models.Model):
    user_id = models.UUIDField()
    game_id = models.UUIDField()

    class Meta:
        managed = False
        db_table = 'users_games'