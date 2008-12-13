from django.template import Library
from simpleblog.models import Post

register = Library()

def get_news(num_of_items):
    news = Post.objects.order_by('-date_added').filter(is_news = True)[:num_of_items]
    return {'news': news}
register.inclusion_tag('news/news.html')(get_news)