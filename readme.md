This is a Grails plugin that allows you to create Workflows using a simplified DSL

## Example

```Groovy
workflow(name : 'onlineReporter'){
	edit{
		writeArticle(to : 'edit'){
               run('rowkService.prepare'){
                    output {
                         article_id(to:'articleId')
                         db_user(to:'loggedinUser')
                    }
               }
			run('articleService.save'){
                    input {
                         id(ref:'articleId')
                         user(ref:'loggedinUser')
                         override(true)
                    }
			}
		}
		submitArticle(to : 'review')
		cancel(to : 'end')
	}
	review {
		ok(to : 'publish')
		keep(to : 'review')
		cancel(to : 'end')
	}
	publish{
		ok(to : 'end')
		reset(to : 'review')
	}
	end
}
```