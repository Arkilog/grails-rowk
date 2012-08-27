package grails.plugins.rowk

class RowkDslService {

	RowkWorkflow createWorkflow(String dsl) {
        def builder = new WorkflowBuilder()
        def workflow = builder.workflow(dsl)
        workflow.save()
        return workflow
	}
	
}