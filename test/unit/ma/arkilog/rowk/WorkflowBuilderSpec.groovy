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
"""
          println DSL
          def builder = new WorkflowBuilder()
          def workflow = builder.workflow(DSL)
          then:
          workflow.events?.name == ['writeArticle','submitArticle','cancel','ok','keep','cancel','ok','reset']
          workflow.states.find{it.name=='edit'}.transitions.nextState.name == ['edit', 'review','end']
          workflow.states.find{it.name=='review'}.transitions.nextState.name == ['publish','review','end']
          workflow.states.find{it.name=='publish'}.transitions.nextState.name == ['end','review']
          workflow.states.find{it.name=='edit'}.transitions[0].actions[0].with{
               results.name == ["user","userEmail"]
               results.ref.name == ['author','authorEmail']
          }
          workflow.states.find{it.name=='edit'}.transitions[0].actions[1].with{
               parameters.name == ["id","user","override"]
               parameters[2].val() == true
               results.name == [null]
               results.ref.name == ["articleVersion"]
               function == 'save'
               service == 'articleService'
          }
          workflow.name == "onlineReporter"
          workflow.variables?.name == ['author', 'authorEmail','articleVersion', 'reviewerName', 'reviewerEmail', 'publishSubjectTemplate', 'publishMailTemplate', 'articleId']
          workflow.states.name == ["edit","review","publish","end"]
          workflow.start.name == "edit"
          !workflow.hasErrors()
	}
}
