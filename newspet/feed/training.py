from python2java import Python2Java
from django.conf import settings
import sys

INCREMENTAL = "INCREMENTAL"
BATCH = "BATCH"
GOOD_OPINION = 1
BAD_OPINION = -1

def train_item(item, category, opinion, trash):
    if opinion is GOOD_OPINION:
        send_train_data(item, category, GOOD_OPINION)
    if opinion is BAD_OPINION:
        send_train_data(item, category, BAD_OPINION)
        send_train_data(item, trash, GOOD_OPINION)

def move_item(item, category, new_category):
    send_train_data(item, new_category, GOOD_OPINION)
    send_train_data(item, old_category, BAD_OPINION)

def send_train_data(item, category, opinion):
    if opinion is GOOD_OPINION:
        message = '%s,%d,%d,%d' % (INCREMENTAL, category.owner.id, category.id, item.id)
        try:
            sock = Python2Java(settings.TRAINER_HOST, settings.TRAINER_PORT)
            sock.send(message)
            return True
        except:
            sys.stderr.write("Connection Failed with message '%s' to Host: %s, Port %d" % (message, settings.TRAINER_HOST, settings.TRAINER_PORT))
    else:
        return False
    
def init_train_category(category, training_set):
    try:
        sock = Python2Java(settings.TRAINER_HOST, settings.TRAINER_PORT)
        sock.send('%s,%d,%d,%d' % (BATCH, category.user.id, category.id, training_set.id))
        return True
    except:
        return False
