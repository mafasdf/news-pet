from django import template

register = template.Library()

@register.inclusion_tag('feed/categories.html')
def show_categories(user):
    categories = user.category_set.all()
    return {'categories': categories}