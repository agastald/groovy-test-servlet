package groovy.test.servlet

class EvalController {

    def index() {
        if (params.'groovy.script') {
            def baos = new ByteArrayOutputStream()
            def printStream = new PrintStream(baos)
            Binding binding = new Binding()
            binding.setProperty("out", printStream)
            binding.setProperty("req", request)
            binding.setProperty("res", response)
            binding.setProperty("grailsApplication", grailsApplication)
            GroovyShell shell = new GroovyShell(binding)
            def result
            try {
                result = shell.evaluate(params.groovy.script)
            } catch (t) {
                if (params.'groovy.servlet.captureOutErr' == 'true') {
                    t.printStackTrace(printStream)
                } else {
                    throw t
                }
            }
            if (params.'groovy.servlet.output' == 'raw') {
                render baos.toString()
                return
            }
            [output: baos.toString(), result: result, evaluate: true]
        } else {
            params.'groovy.script' = 'println "hello!"'
        }
    }

}
