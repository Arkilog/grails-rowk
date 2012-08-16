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
          //Transition definition (to state 'dispatch')
          updateArticle(to:'dispatch'){
             //Action definition
               run('articleService.update'){
                  //Action parameter definition (using variable)
                    id(ref:'params.id')
                    user(ref:'session.user')
                  //Action parameter definition (using constant)
                    override true
                  //Action result definition (whole result)
                    articleVersion
               }
               //No need to assign for a system state

               //Optional 'For your information' list (notification list)
               fyi{
                    role('authors')
               }
          }
          createArticle(to:'dispatch'){
               run('articleService.save'){
                    id(ref:'params.id')
                    user(ref:'session.user')
                    override true
                    articleVersion
               }
               //No need to assign for a system state

               //As mentionnted FYI can be used but not mandatory
               fyi{
                    role('authors')
               }
          }
          cancel(to:'end')
     }
     //State definition (AndFork pattern) 
     //it's a system state no need to specify assignmments
     dispatch(type:'andfork'){
          requestControl(to :'control'){
               //'First in first out' assignment, the first participant that takes 
               // control of the following state will make the process progress
               //Mandatory here
               assign(type:'fifo'){
                    user(ref:'oldAuthor')
                    //group('johndoesworldwide')
                    //role('authors')
               }

          }
          requestReview(to:'review'){
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
     }
     //State definition
     control {
          askForRewrite(to :'start'){
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
          sendComments(to:'dispatch')
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
               //No need to assign the end of the process
          }
          lastMinuteComments(to:'dispatch'){
               run('articleService.addComments'){
                    reviewer(ref:'reviewerName')
                    reviewerEmail(ref:'reviewerEmail')
                    id(ref:'articleId')
                    comments(ref:'lastMinuteComments')
               }
               //No need to assign for a system state
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
          workflow.states.find{it.name=='dispatch'}.with {
               transitions[0].nextState.name == 'control'
               transitions[1].nextState.name == 'review';
          }
          def startT1 = workflow.states.find{it.name=='dispatch'}.transitions[1]
          startT1.assignment.assignees*.val() == ['johndoe','johndoesworldwide','authors']
          startT1.fyi.assignees*.val() == [null,'johndoesworldwide','authors']
          
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['publish','dispatch']
          workflow.states.find{it.name=='publish'}.transitions.nextState.name == ['end','dispatch']
          workflow.states.find{it.name=='start'}.transitions[1].actions[0].with{
               parameters.name == ["id","user","override"]
               parameters[2].val() == true
               parameters[2].val() == true
               results.name == [null]
               results.ref.name == ["articleVersion"]
               function == 'save'
               service == 'articleService'
          }
          workflow.name == "onlineReporter"
          workflow.variables?.name == ['id', 'user', 'articleVersion', 'oldAuthor', 'author',
           'authorEmail', 'reviewerName', 'reviewerEmail', 'articleId', 
           'publishSubjectTemplate', 'publishMailTemplate', 'lastMinuteComments']
          workflow.variables?.find{it.name=='user'}.source == 'session'
          workflow.variables?.find{it.name=='id'}.source == 'params'
          workflow.states.name == ["start","dispatch","control","review","publish","end"]
          workflow.start.name == "start"
          !workflow.hasErrors()
	}
}
