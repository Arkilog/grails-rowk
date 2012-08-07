package ma.arkilog.rowk

import grails.test.mixin.*
import grails.plugin.spock.UnitSpec

@TestFor(RowkWorkflow) 
class RowkWorkflowSpec extends UnitSpec {
    def "workflow creation"() {
          setup:
          mockDomain(RowkAction)
          mockDomain(RowkActivity)
          mockDomain(RowkContext)
          mockDomain(RowkEvent)
          mockDomain(RowkParticipant)
          mockDomain(RowkProcess)
          mockDomain(RowkState)
          mockDomain(RowkTransition)

          when:
          def workflow = new RowkWorkflow()
          def state0 = new RowkState()
          def state2 = new RowkState()
          workflow.name = 'demo'
          workflow.addToStates(state0).addToStates(state2)
          def trans1 = new RowkTransition(nextState:state2)
          state0.addToTransitions(trans1)
          workflow.start = state0
          workflow.validate()
          then:
          !workflow.hasErrors()
	}
}
