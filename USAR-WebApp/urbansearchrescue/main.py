import os
from google.appengine.ext.webapp import template

import cgi

from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from google.appengine.ext import db

   

class House(db.Model):
    author = db.UserProperty(required = True)
    date_entered = db.DateTimeProperty(auto_now_add=True, required=True)

    # field data (X data)
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
        self.response.out.write("""
                  <form action="/upload" enctype="multipart/form-data" method="post">
                    <div><input type="file" name="img"/></div>
                    <div><input type="submit" value="Send Pic"></div>
                  </form>
                </body>
              </html>""") 

class Upload(webapp.RequestHandler):
    def post(self):
        user = users.get_current_user()
        if not user:
            self.redirect(users.create_login_url(self.request.uri))

        houseimage = HouseImage()
        image = self.request.get("img")
        houseimage.image = db.Blob(image)
        houseimage.put()
        self.redirect('/uploadform')


class MainPage(webapp.RequestHandler):
    def get(self):
        for img in HouseImage.all():
            self.response.out.write("<div><img src='img?img_id=%s'></img>" %
                                          img.key())
#        could use this later
#        path = os.path.join(os.path.dirname(__file__), 'show_imgs.html')
#        self.response.out.write(template.render(path, template_values))



class Image (webapp.RequestHandler):
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
                                      ('/img', Image)],
                                     debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()
