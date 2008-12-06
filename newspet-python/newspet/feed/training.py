from python2java import Python2Java
from django.conf import settings
import threading
import sys

INCREMENTAL = "INCREMENTAL"
BATCH = "BATCH"
GOOD_OPINION = 1
BAD_OPINION = -1

def train_item(item, category, opinion):
    if opinion is GOOD_OPINION:
        send_train_data(item, category, GOOD_OPINION)
    if opinion is BAD_OPINION:
        send_train_data(item, category, BAD_OPINION)

def move_item(item, category, new_category):
    send_train_data(item, new_category, GOOD_OPINION)
    send_train_data(item, old_category, BAD_OPINION)

def send_train_data(item, category, opinion):
    if opinion is GOOD_OPINION:
        message = '%s,%d,%d,%d' % (INCREMENTAL, category.owner.id, category.id, item.id)
        host, port = settings.TRAINER_HOST, settings.TRAINER_PORT
        TrainingThread(message, host, port).start()
    else:
        return False
    
def train_category(category, training_set):
    message = '%s,%d,%d,%d' % (BATCH, category.user.id, category.id, training_set.id)
    host, port = settings.TRAINER_HOST, settings.TRAINER_PORT
    TrainingThread(message, host, port).start()
    
class TrainingThread(threading.Thread):
    
    def __init__(self, message, host, port, *args, **kwargs):
        self.message = message
        self.host = host
        self.port = port
        super(TrainingThread, self).__init__(*args, **kwargs)
    
    def run(self):
        try:
            sock = Python2Java(self.host, self.port)
            sock.send(self.message)
        except:
            sys.stderr.write("Connection Failed with message '%s' to Host: %s, Port %d" % (self.message, self.host, self.port))

