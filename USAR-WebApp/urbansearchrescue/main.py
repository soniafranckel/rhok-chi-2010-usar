import os
from google.appengine.ext.webapp import template

import cgi

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

from datetime import datetime


def require_login(handler):
    user = users.get_current_user()
    if not user:
        handler.redirect(users.create_login_url(handler.request.uri))
       
   

class House(db.Model):
    date_created = db.DateTimeProperty(auto_now_add=True) # the date the house was entered into the database

    # The person entering the data
    updated_by = db.UserProperty()
    date_updated = db.DateTimeProperty()

    # The person on the field. field data (data around the X mark on the wall)
    num_people = db.IntegerProperty() # default Null
    condition = db.StringProperty(multiline=False) # default Null
    date_found = db.DateTimeProperty() # default Null
    agency = db.StringProperty(multiline=False) # default Null
    


# in case we need multiple images per house, and each has some sort of metadata
class HouseImage(db.Model):
    house = db.ReferenceProperty(House, collection_name='images', required=False) # should be required eventually
    image = db.BlobProperty()



class UploadForm(webapp.RequestHandler):
    def get(self):
        self.response.out.write("""<html><body>
                  <form action="/upload" enctype="multipart/form-data" method="post">
                    <div><input type="file" name="img"/></div>
                    <div><input type="submit" value="Send Pic"></div>
                  </form>
                </body>
              </html>""") 

class Upload(webapp.RequestHandler):
    def post(self):
        require_login(self)

        houseimage = HouseImage()
        image = self.request.get("img")
        houseimage.image = db.Blob(image)
        houseimage.put()
        self.redirect('/uploadform')


class MainPage(webapp.RequestHandler):
    def get(self):
        self.redirect("/houseinfo")

class NewHouse (webapp.RequestHandler):
    form = """%i houses <html><body>
                  <form action="/newhouse" enctype="multipart/form-data" method="post">
                    <div><input type="submit" value="New House"></div>
                  </form>
                </body>
              </html>""" % House.all().count()

    def post(self): # just make a new house
        House().put()
        self.response.out.write(self.form)

    def get(self):
        self.response.out.write(self.form)


class UpdateHouse (webapp.RequestHandler):
    def post(self):
        require_login(self)

        house = db.get(self.request.get('house_id'))

        house.updated_by = user
        house.date_updated = datetime.now()

        if self.request.get('num_people'):
            house.num_people = int(self.request.get('num_people'))
        if self.request.get('condition'):
            house.condition = self.request.get('condition')
        if self.request.get('date_found'):
            house.date_found = self.request.get('date_found')
        if self.request.get('agency'):
            house.agency = self.request.get('agency')

        house.put()

        self.redirect('/houseinfo/?house_id' % house.key)

class HouseInfo (webapp.RequestHandler):
    def get(self):
        if self.request.get('house_id'):
            self.showhouse()
        else:
            self.listhouses()

    def showhouse(self):
        house = db.get(self.request.get("house_id"))
        path = os.path.join(os.path.dirname(__file__), 'houseinfo.html')
        self.response.out.write(template.render(path, {"house": house}))

    def listhouses(self):
        path = os.path.join(os.path.dirname(__file__), 'listhouses.html')
        self.response.out.write(template.render(path, {"houses": House().all()}))



class ImageServe (webapp.RequestHandler):
    def get(self):
        houseimage = db.get(self.request.get("img_id"))
     
        if houseimage.image:
            self.response.headers['Content-Type'] = "image/jpg"
            self.response.out.write(houseimage.image)
        else:
            self.error(404)


application = webapp.WSGIApplication(
                                     [('/', MainPage),
                                      ('/uploadform', UploadForm),
                                      ('/upload', Upload),
                                      ('/img', ImageServe),
                                      ('/newhouse', NewHouse),
                                      ('/houseinfo', HouseInfo),
                                      ('/updatehouse', UpdateHouse)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
