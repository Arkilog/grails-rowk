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
          mockDomain(RowkTarget)
          mockDomain(RowkAssignee)
          mockDomain(RowkFyiTarget)
          mockDomain(RowkFifoTarget)

          when:
          def DSL = """
//Workflow definition
workflow(name :'onlineReporter'){
     //State definition
    start{
          //Transition definition (to state 'edit')
          updateArticle(to:'edit'){
             //Action definition
               run('rowkService.userInfo'){
                  //Action result definition
                    user(to:'author')
                    userEmail(to:'authorEmail')
               }
               run('articleService.update'){
                  //Action parameter definition (using variable)
                    id(ref:'articleId')
                    user(ref:'author')
                  //Action parameter definition (using constant)
                    override true
                  //Action result definition (whole result)
                    articleVersion
               }
               assign(type:'fifo'){
                    user(ref:'oldAuthor')
                    //group('johndoesworldwide')
                    //role('authors')
               }
               fyi{
                    //user(ref:'oldAuthor')
                    //group('johndoesworldwide')
                    role('authors')
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
               assign(type:'fifo'){
                    user('johndoe')
                    group('johndoesworldwide')
                    role('authors')
               }
               fyi{
                    user(ref:'oldAuthor')
                    group('johndoesworldwide')
                    role('authors')
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern)
     edit(type:'andfork'){
          requestControl(to :'control')
          requestReview(to:'review')
     }
     //State definition
     control {
          askForRewrite(to :'edit'){
               run('bossService.angry')
               assign(type:'vote',minpercent:100){
                    user('bigboss')
                    group('masterauthors')
                    role('experiencedauthors')
               }
          }
          approve(to :'publish'){
               run('bossService.happy')
               assign(type:'vote',minpercent:100){
                    user('bigboss')
                    group('masterauthors')
                    role('experiencedauthors')
               }
          }
          abort(to:'end')
     }
     //State definition
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
               assign(type:'serial'){
                    user('teamleader')
                    user('manager')
                    user('bigboss')
               }
          }
          sendComments(to:'edit')
     }
     //State definition (AndJoin pattern)
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
     //State definition (an end state is mandatory)
     end
}
"""
          println DSL
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(DSL)
          then:
          workflow.events?.name == [
          'updateArticle', 'createArticle', 
          'cancel', 'requestControl', 
          'requestReview', 'askForRewrite',
          'approve', 'abort', 'ok', 'sendComments', 
          'ok', 'lastMinuteComments'
          ]
          workflow.states.find{it.name=='edit'}.with {
               transitions[0].nextState.name == 'control'
               transitions[1].nextState.name == 'review';
          }
          def startT1 = workflow.states.find{it.name=='start'}.transitions[1]
          startT1.assignment.assignees*.val() == ['johndoe','johndoesworldwide','authors']
          startT1.fyi.assignees*.val() == [null,'johndoesworldwide','authors']
          
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['publish','edit']
          workflow.states.find{it.name=='publish'}.transitions.nextState.name == ['end','edit']
          workflow.states.find{it.name=='start'}.transitions[0].actions[0].with{
               results.name == ["user","userEmail"]
               results.ref.name == ['author','authorEmail']
          }
          workflow.states.find{it.name=='start'}.transitions[1].actions[1].with{
               parameters.name == ["id","user","override"]
               parameters[2].val() == true
               parameters[2].val() == true
               results.name == [null]
               results.ref.name == ["articleVersion"]
               function == 'save'
               service == 'articleService'
          }
          workflow.name == "onlineReporter"
          workflow.variables?.name == ['author', 'authorEmail', 'articleId', 'articleVersion', 'oldAuthor', 'reviewerName', 'reviewerEmail', 'publishSubjectTemplate', 'publishMailTemplate', 'lastMinuteComments']
          workflow.states.name == ["start","edit","control","review","publish","end"]
          workflow.start.name == "start"
          !workflow.hasErrors()
	}
}
