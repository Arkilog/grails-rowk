This is a Grails plugin that allows you to create Workflows using a simplified DSL

## Example

```Groovy
workflow(name : 'onlineReporter'){
	edit{
		writeArticle(to : 'edit'){
			run('articleService.save'){id->
				articleId = id
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