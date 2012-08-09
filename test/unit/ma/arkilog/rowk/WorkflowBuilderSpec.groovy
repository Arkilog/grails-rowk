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
"""
          println DSL
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(DSL)
          then:
          workflow.name == "onlineReporter"
          workflow.variables?.name == ['articleId','loggedinUser']
          workflow.states.name == ["edit","review","publish","end"]
          workflow.start.name == "edit"
          workflow.events?.name == ['writeArticle','submitArticle','cancel','ok','keep','cancel','ok','reset']
          workflow.states.find{it.name=='edit'}.transitions.nextState.name == ['edit', 'review','end']
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['publish','review','end']
          workflow.states.find{it.name=='publish'}.transitions.nextState.name == ['end','review']
          workflow.states.find{it.name=='edit'}.transitions[0].actions[1].function == 'save'
          workflow.states.find{it.name=='edit'}.transitions[0].actions[1].service == 'articleService'
          workflow.states.find{it.name=='edit'}.transitions[0].actions[1].parameters.name == ["id","user","override"]
          workflow.states.find{it.name=='edit'}.transitions[0].actions[0].results.name == ["article_id","db_user"]
          workflow.states.find{it.name=='edit'}.transitions[0].actions[0].results.ref.name == ['articleId','loggedinUser']
          !workflow.hasErrors()
	}
}
