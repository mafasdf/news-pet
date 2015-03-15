ALTER TABLE feed\_category ADD position2 integer NOT NULL DEFAULT 1;

python manage.py shell\_plus

for user in User.objects.all():
> counter = 0
> for cat in Category.objects.filter(owner=user):
> > cat.position = counter
> > cat.save()
> > counter += 1