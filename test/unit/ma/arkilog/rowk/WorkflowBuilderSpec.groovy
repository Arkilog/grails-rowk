package ma.arkilog.rowk

import grails.test.mixin.*
import grails.plugin.spock.UnitSpec

class WorkflowBuilderSpec extends UnitSpec {
    def "workflow dsl"() {
          setup:
          mockDomain(RowkAction)
          mockDomain(RowkActivity)
          mockDomain(RowkContext)
          mockDomain(RowkEvent)
          mockDomain(RowkParticipant)
          mockDomain(RowkProcess)
          mockDomain(RowkState)
          mockDomain(RowkTransition)
          mockDomain(RowkWorkflow)

          when:
          def DSL = """
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
"""
          println DSL
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(DSL)
          then:
          workflow.events?.name == [
          'selectArticle', 'createArticle', 
          'cancel', 'requestControl', 
          'requestReview', 'askForRewrite',
          'approve', 'autodecide', 
          'abort', 'ok', 'sendComments', 
          'ok', 'lastMinuteComments'
          ]
          println 'go'
          workflow.states.find{it.name=='edit'}.with {
               transitions[0].nextState.name == 'control'
               transitions[1].nextState.name == 'review';
          }
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['publish','edit']
          workflow.states.find{it.name=='publish'}.transitions.nextState.name == ['end','edit']
          workflow.states.find{it.name=='start'}.transitions[0].actions[0].with{
               results.name == ["user","userEmail"]
               results.ref.name == ['author','authorEmail']
          }
          workflow.states.find{it.name=='start'}.transitions[1].actions[1].with{
               parameters.name == ["id","user","override"]
               parameters[2].val() == true
               results.name == [null]
               results.ref.name == ["articleVersion"]
               function == 'save'
               service == 'articleService'
          }
          workflow.name == "onlineReporter"
          workflow.variables?.name == ['author', 'authorEmail', 'articleVersion', 'author', 'authorEmail', 'articleVersion', 'reviewerName', 'reviewerEmail', 'publishSubjectTemplate', 'publishMailTemplate', 'articleId', 'lastMinuteComments']
          workflow.states.name == ["start","edit","control","review","publish","end"]
          workflow.start.name == "start"
          !workflow.hasErrors()
	}
}
