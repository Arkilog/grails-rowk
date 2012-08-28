package grails.plugins.rowk

import grails.plugin.spock.UnitSpec

class WorkflowBuilderSpec extends UnitSpec {
    def "Workflow complete dsl manual validation"() {
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
          def DSL = SampleDSL.ONLINE_REPORTER
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(DSL)
          then:
          workflow.events?.name == [
          'updateArticle', 'createArticle', 
          'cancel', 'requestControl', 
          'requestReview', 'askForRewrite',
          'approve', 'abort', 'ok', 'sendComments', 
          'go', 'ok', 'lastMinuteComments'
          ]
          def dispatch = workflow.states.find{it.name=='dispatch'}
          dispatch.transitions[0].nextState.name == 'control'
          dispatch.transitions[1].nextState.name == 'review'
          dispatch.transitions[1].fyi.assignees*.val() == ['elderManagement']
          def control = workflow.states.find{it.name=='control'}
          control.assignment.assignees*.val() == ['bigboss', 'masterauthors', 'experiencedauthors']
          
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['syncadvices','start']
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
          workflow.variables?.name == ['oldAuthor', 'id', 'user', 'articleVersion', 'author', 
           'authorEmail', 'reviewerName', 'reviewerEmail', 'articleId', 
           'publishSubjectTemplate', 'publishMailTemplate', 'lastMinuteComments']
          workflow.variables?.find{it.name=='user'}.source == 'session'
          workflow.variables?.find{it.name=='id'}.source == 'params'
          workflow.states.name == ["start","dispatch","control","review",'syncadvices',"publish","end"]
          workflow.start.name == "start"
          !workflow.hasErrors()
          workflow.validate(failOnError:true)
	}
    def "Workflow complete dsl validation"() {
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
          mockForConstraintsTests(RowkWorkflow)
          mockForConstraintsTests(RowkHumanState)
          mockForConstraintsTests(RowkEndState)
          mockForConstraintsTests(RowkTransition)
          when:
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(dsl)
          then:
          validate(workflow,!valid)
          workflow.hasErrors() == !valid
          where:
          dsl | valid
          SampleDSL.ONLINE_REPORTER | true

     }
    def "Workflow manual creation"() {
          setup:
          mockDomain(RowkAction)
          mockDomain(RowkActivity)
          mockDomain(RowkContext)
          mockDomain(RowkEvent)
          mockDomain(RowkParticipant)
          mockDomain(RowkProcess)
          mockDomain(RowkHumanState)
          mockDomain(RowkTransition)
          mockDomain(RowkWorkflow)
          mockDomain(RowkState)
          mockForConstraintsTests(RowkWorkflow)
          mockForConstraintsTests(RowkHumanState)
          mockForConstraintsTests(RowkEndState)
          mockForConstraintsTests(RowkTransition)
          when:
          def workflow = new RowkWorkflow()
          def state0 = new RowkHumanState()
          def state2 = new RowkHumanState()
          workflow.name = 'demo'
          workflow.addToStates(state0).addToStates(state2)
          def trans1 = new RowkTransition(nextState:state2)
          state0.addToTransitions(trans1)
          workflow.start = state0
          workflow.validate()
          then:
          !workflow.hasErrors()
     }

     def "Basic workflow constraints"() {
          setup:
          mockDomain(RowkAction)
          mockDomain(RowkActivity)
          mockDomain(RowkEvent)
          mockDomain(RowkHumanState)
          mockDomain(RowkTransition)
          mockDomain(RowkWorkflow)
          mockDomain(RowkFifoTarget)
          mockDomain(RowkRole)
          mockForConstraintsTests(RowkWorkflow)
          mockForConstraintsTests(RowkHumanState)
          mockForConstraintsTests(RowkEndState)
          mockForConstraintsTests(RowkTransition)
          mockForConstraintsTests(RowkFifoTarget)
          mockForConstraintsTests(RowkRole)

          when:
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(dsl)

          then:
          validate(workflow,wValid) == wValid
          validate(workflow.states,sValid) == sValid
          validate(workflow.states.transitions,tValid) == tValid
          workflow.states.find{it.name=="end"}
          where:

          dsl | wValid | sValid | tValid
          """
          workflow(name:'todo1'){
               start{
                    add(to:'start')
                    finish(to:'end')
                    assignment{
                         role('workers')
                    }
               }
          }
          """ | true | true | true
          """
          workflow(name:'todo2'){
               start{
                    add(to:'start')
                    finish(to:'end')
                    assignment{
                         role('workers')
                    }
               }
               end
          }
          """ | true | true | true
          """
          workflow(name:'todo3'){
               start{
                    add(to:'start')
                    finish(to:'end')
               }
               end
          }
          """ | true | false | true
          """
          workflow(name:'todo4'){
               start{
                    add(to:'404')
                    finish(to:'end')
                    assignment{
                         role('workers')
                    }
               }
          }
          """ | true | true | false

     }
     static _validate(obj){
          if (obj instanceof Collection){
               obj.findAll{it}.flatten().findAll{it}.every{
                    it.validate()
               }
          } else {
               obj?.validate()
          }
          
     }
     static validate(it,expectedError=true){
          boolean result = _validate(it)
          if (!result && expectedError) {
               println errors(it)
          }
          result
     }
     static errors(Collection list){
          list.findAll{it}.flatten().findAll{it}.collect{errors(it)}.join("\n")
     }
     static errors(obj){
          def out = new StringWriter()
          out << "ERRORS FOR (${obj?.getClass().simpleName}) : ($obj):\n"
          obj.errors.allErrors.each{
               def str = it.defaultMessage
               it.arguments.eachWithIndex{arg,i->
                    str = str.replace("{$i}".toString(),arg?.toString())
               }
               out << "${str}\n"
          }
          out.toString()
     }

}
