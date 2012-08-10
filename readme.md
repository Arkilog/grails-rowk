[![Build Status](https://secure.travis-ci.org/yellowsnow/rowk.png?branch=master)](http://travis-ci.org/yellowsnow/rowk)


This is a Grails plugin that allows you to create Workflows using a simplified DSL

## Example

```Groovy
workflow(name :'onlineReporter'){
	edit{
		writeArticle(to :'edit'){
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
		submitArticle(to:'review')
		cancel(to:'end')
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
			run('mailService.sendPublishNotification'){
				from(ref:'reviewerEmail')
				destination(ref:'authorEmail')
				subject(ref:'publishSubjectTemplate')
				body(ref:'publishMailTemplate')
			}
		}
		keep(to:'review')
		cancel(to:'end')
	}
	publish{
		ok(to:'end')
		reset(to:'review')
	}
	end
}
```