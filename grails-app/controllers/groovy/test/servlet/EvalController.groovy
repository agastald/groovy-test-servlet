package groovy.test.servlet

class EvalController {

    private restraintProductionAccess() {
        if (grails.util.Environment.current == grails.util.Environment.PRODUCTION
                && request.getHeader('x-access-word') != grailsApplication.config.testPassword) {
            response.sendError(404)
        }
    }

    def index() {
        restraintProductionAccess()
        if (params.'groovy.script') {
            def baos = new ByteArrayOutputStream()
            def printStream = new PrintStream(baos)
            Binding binding = new Binding()
            binding.setProperty('out', printStream)
            binding.setProperty('req', request)
            binding.setProperty('res', response)
            binding.setProperty('grailsApplication', grailsApplication)
            GroovyShell shell = new GroovyShell(grailsApplication.classLoader,binding)
            def result
            try {
                result = shell.evaluate(params.groovy.script)
                params.'groovy.script' = params.'groovy.script'.trim()
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
            params.'groovy.servlet.captureOutErr' = 'true'
        }
    }

    def status() {
        restraintProductionAccess()
        def rtBean = java.lang.management.ManagementFactory.getRuntimeMXBean()
        def memBean = java.lang.management.ManagementFactory.getMemoryMXBean()
        def heap = (memBean.getHeapMemoryUsage().getCommitted() / 1048576).toInteger()
        def nonHeap = (memBean.getNonHeapMemoryUsage().getCommitted() / 1048576).toInteger()
        def map = [
            localhost: InetAddress.getLocalHost(),
            environment: grails.util.Environment.current.name,
            startupTime: new Date(rtBean.startTime).format('yy/MM/dd HH:mm:ss'),
            appStartupTime: new Date(grailsApplication.mainContext.startupDate).format('yy/MM/dd HH:mm:ss'),
            currentTime: new Date().format('yy/MM/dd HH:mm:ss'),
            duration: groovy.time.TimeCategory.minus(new Date(), new Date(rtBean.startTime)).toString(),
            appDuration: groovy.time.TimeCategory.minus(new Date(), new Date(grailsApplication.mainContext.startupDate)).toString(),
            groovy: GroovySystem.version,
            java: Runtime.package.implementationVersion,
            grails: grailsApplication.metadata.sort(),
            memory: [heap: heap, nonHeap: nonHeap, total: (heap + nonHeap)],
            encoding: [defaultCharset: java.nio.charset.Charset.defaultCharset.toString(), 'file.encoding': System.getProperty('file.encoding')],
            os: System.getProperties().findAll { it.key ==~ /^os\..*/ }.sort(),
            arguments: rtBean.inputArguments,
            pid: java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0],
        ]
        [map:map]
    }

    def raw() {
        response.contentType = 'text/plain'
        response.characterEncoding = request.characterEncoding
        def content = request.inputStream?.text ?: request.queryString
        if (!content) content = request.dump()
        def str = "${request.method} ${content.size()}/${request.contentLength} ${request.contentType}\n${content}\n"
        print "${'-'*80}\n$str${'-'*80}\n"
        render str
    }

    def headers() {
        response.contentType = 'text/plain'
        response.characterEncoding = request.characterEncoding
        def headers = request.headerNames.toList().sort().collectEntries { name ->
            [name, request.getHeaders(name).toList().join(',')]
        }
        def max = headers.keySet().max { it.size() }.size()
        def str = headers.collect { k, v ->
            "${k.padLeft(max)} : $v"
        }.join('\n') + '\n'
        print "${'='*80}\n$str${'='*80}\n"
        render str
    }

}
