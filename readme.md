[![Build Status](https://secure.travis-ci.org/yellowsnow/rowk.png?branch=master)](http://travis-ci.org/yellowsnow/rowk)


This is a Grails plugin that allows you to create Workflows using a simplified DSL

## Example

```Groovy
workflow(name :'onlineReporter'){
     start{
          selectArticle(to:'edit'){
               run('rowkService.userInfo'){
                    user(to:'author')
                    userEmail(to:'authorEmail')
               }
               run('articleService.update'){
                    id(ref:'articleId')
                    user(ref:'author')
                    override true
                    articleVersion
               }
          }
          createArticle(to:'edit'){
               run('rowkService.userInfo'){
                    user(to:'author')
                    userEmail(to:'authorEmail')
               }
               run('articleService.save'){
                    id(ref:'articleId')
                    user(ref:'author')
                    override true
                    articleVersion
               }
          }
          cancel(to:'end')
     }
	edit(type:'andfork'){
		requestControl(to :'control')
		requestReview(to:'review')
	}
     control {
          askForRewrite(to :'edit'){
               run('bossService.angry')
          }
          approve(to :'publish'){
               run('bossService.happy')
          }
          autodecide(to :['publish','edit']){
               route('textService.parseArticle'){
                    id(ref:'articleId')
                    version(ref:'articleVersion')
               }
          }
          abort(to:'end')
     }
     review {
          ok(to :'publish'){
               run('rowkService.userInfo'){
                    user(to:'reviewerName')
                    userEmail(to:'reviewerEmail')
               }
               run('articleService.preparePublishMailTemplate'){
                    user(ref:'author')
                    mail(ref:'authorEmail')
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    version(ref:'articleVersion')
                    subjectTemplate(to:'publishSubjectTemplate')
                    mailTemplate(to:'publishMailTemplate')
               }
          }
          sendComments(to:'edit')
     }
	publish(type:'andjoin'){
		ok(to:'end'){
               run('mailService.sendPublishNotification'){
                    from(ref:'reviewerEmail')
                    destination(ref:'authorEmail')
                    subject(ref:'publishSubjectTemplate')
                    body(ref:'publishMailTemplate')
               }
          }
		lastMinuteComments(to:'edit'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
          }
	}
	end
}
```
Please note that the DSL Language is still moving untill the 1.0.0 stable release