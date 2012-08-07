package ma.arkilog.rowk

class WorkflowBuilder {
	
	static workflow(String dsl) {
		def dslScript = new GroovyShell().parse(dsl)
		
		def workflow = new RowkWorkflow()
		dslScript.metaClass = createEMC(dslScript.class, {
			ExpandoMetaClass emc ->
			
			
			emc.workflow = {map,Closure cl ->
				workflow.name  = map.name
				cl.delegate = new WorkflowDelegate(workflow)
				cl.resolveStrategy = Closure.DELEGATE_FIRST
				cl()
			}
		})
		dslScript.run()
		return workflow
	}
	
	static ExpandoMetaClass createEMC(Class scriptClass, Closure cl) {
		ExpandoMetaClass emc = new ExpandoMetaClass(scriptClass, false)
		
		cl(emc)
		
		emc.initialize()
		return emc
	}
}
class Linker{
	private nonLinked = [:]
	def srcName
	def tgtName
	def add(missing, link){
		def list = nonLinked[link] ?: []
		list << missing
		nonLinked[link] = list
	}
	def match(target){
		nonLinked[target."$tgtName"].each{
			it."$srcName" = target
		}
	}
	def matchAll(targets){targets.each{match(it)}}
}
class WorkflowDelegate {
	private RowkWorkflow workflow
	private nextStateLinker = new Linker(srcName:'nextState',tgtName:'name')
	
	WorkflowDelegate(RowkWorkflow workflow) {
		this.workflow = workflow
	}
	
	def methodMissing(String name, Object args) {
		if (name) {
			def state = new RowkState()
			state.name = name
			workflow.addToStates(state)
			workflow.start = (workflow.start ?: state)
			if (args?.length ==1 && args[0] instanceof Closure) {
				args[0].delegate = new RowkStateDelegate(workflow, state,nextStateLinker)
				args[0].resolveStrategy = Closure.DELEGATE_FIRST
				args[0]()
			}
		} else {
			throw new MissingMethodException(name, this.class, args as Object[])
		}
	}
	void end() {
		def state = new RowkState()
		state.name = "end"
		workflow.addToStates(state)
		nextStateLinker.matchAll(workflow.states)
	}
	
	def propertyMissing(String name, value) {
		if (name=='name'){
			workflow.name = String.valueOf(value)
		} else if (name=='end'){
			end()
		}
		return null
	}
}

class RowkStateDelegate {
	private RowkState state
	private RowkWorkflow workflow
	private linker
	
	RowkStateDelegate(RowkWorkflow workflow, RowkState state, Linker linker) {
		this.state = state
		this.workflow = workflow
		this.linker = linker
	}
	
	void methodMissing(String name, Object args) {
		if (name && args.length>=1) {
			def event = new RowkEvent()
			event.name = name
			workflow.addToEvents(event)
			def transition = new RowkTransition()
			state.addToTransitions(transition)
			if (args[0] instanceof Map) {
				if (args[0].to) {
					linker.add(transition, args[0].to)
				}
			}
			if (args.length==2){
				if (args[1] && args[1] instanceof Closure) {
					args[1].delegate = new RowkTransitionDelegate(workflow, state, event, transition)
					args[1].resolveStrategy = Closure.DELEGATE_FIRST
					args[1]()
				}
			}
		}
	}
	
	def propertyMissing(String name) {
		return null
	}
}

class RowkTransitionDelegate {
	private RowkState state
	private RowkWorkflow workflow
	private RowkEvent event
	private RowkTransition transition

	RowkTransitionDelegate(RowkWorkflow workflow, RowkState state, RowkEvent event, RowkTransition transition) {
		this.event = event
		this.workflow = workflow
		this.state = state
		this.transition = transition
	}
	
	void methodMissing(String name, Object args) {
		if (name=="run" && args.length>=1){
			def action = new RowkAction()
			transition.addToActions(action)
			if (args[0].indexOf('.')>=0) {
				action.service = args[0].split('\\.')[0]
				action.function = args[0].split('\\.')[1]
			}
			if (args.length==2){
				def closure = args[1]
				if (closure instanceof Closure) {
					closure.delegate = new RowkActionDelegate(workflow, state, event, transition, action)
					closure.resolveStrategy = Closure.DELEGATE_FIRST
					closure()
				}
			}
		}
	}
	def propertyMissing(String name, value) {
		return null
	}
	
}

class RowkActionDelegate {
	private RowkState state
	private RowkWorkflow workflow
	private RowkEvent event
	private RowkTransition transition
	private RowkAction action
	
	RowkActionDelegate(RowkWorkflow workflow, RowkState state, RowkEvent event, RowkTransition transition, RowkAction action) {
		this.event = event
		this.workflow = workflow
		this.state = state
		this.transition = transition
		this.action = action
	}
	
	void methodMissing(String name, Object args) {
	}
	def propertyMissing(String name, value) {
		return null
	}
	
}