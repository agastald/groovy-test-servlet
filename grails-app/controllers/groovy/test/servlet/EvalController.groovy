package groovy.test.servlet

class EvalController {

    private restraintProductionAccess() {
        if (grails.util.Environment.current == grails.util.Environment.PRODUCTION
                && request.getHeader('x-access-word') != grailsApplication.config.eval.testPassword) {
            response.sendError(404)
        }
    }

    static String libUrl = calcLibUrl()

    String lastInclude

    private String importLib() {
        if (params.'groovy.servlet.lib') {
            try {
                UrlFile urlFile = new UrlFile(libUrl,'/tmp/odl/dkl/xpt')
                String baseScript = lastInclude ? urlFile.remoteText : urlFile.text
                if (baseScript) {
                    def baseClass = grailsApplication.classLoader.parseClass baseScript
                    lastInclude = "@groovy.transform.BaseScript ${baseClass.name} _mainScript;"
                } else if (lastInclude) {
                    lastInclude
                } else {
                    "println(':: LIB NOT FOUND: $libUrl'); return;"
                }
            } catch (Throwable t) {
                "println(''':: LIB ERROR:\n$t\n'''); return;"
            }
        } else {
            ''
        }
    }

    def index() {
        restraintProductionAccess()
        if (params.'groovy.script') {
            def baos = new ByteArrayOutputStream()
            def printStream = new PrintStream(baos)
            Binding binding = new Binding()
            binding.setProperty('out', printStream)
            binding.setProperty('request', request)
            binding.setProperty('response', response)
            binding.setProperty('log', log)
            binding.setProperty('controller', this)
            binding.setProperty('grailsApplication', grailsApplication)
            binding.setProperty('ga', grailsApplication)
            String include = importLib()
            GroovyShell shell = new GroovyShell(grailsApplication.classLoader,binding)
            def result
            try {
                result = shell.evaluate(include + params.groovy.script)
                params.'groovy.script' = params.'groovy.script'.trim()
            } catch (org.codehaus.groovy.control.MultipleCompilationErrorsException cex) {
                result = cex.toString()
            } catch (Throwable t) {
                if (params.'groovy.servlet.captureOutErr' == 'true') {
                    t.printStackTrace(printStream)
                } else {
                    throw t
                }
            }
            // if (params.'groovy.servlet.output' == 'raw') {
                response.contentType = 'text/plain; charset=utf-8'
                String streamResult = baos.toString().trim()
                String output
                if (streamResult && result) output = "$streamResult\n$result"
                else output = streamResult ?: result
                render output ?: ''
                return
            // }
            [output: baos.toString(), result: result, evaluate: true]
        } else {
            params.'groovy.script' = 'println "hello!"'
            params.'groovy.servlet.captureOutErr' = 'true'
            [:]
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

    def plain() {
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

    private withController(Closure closure) {
        closure.delegate = this
        closure()
    }

    static String calcLibUrl() {
        if (InetAddress.localHost.hostName =~ 'macale.local|notale|localhost') {
            return 'http://127.0.0.1:7080/ale.gtp'
        }
        libMap[InetAddress.localHost.hostAddress] ?: 'http://aironbrasil.com.br/ale.gtp'
    }

    static Map getLibMap() {
        EvalController.getResource('/lib_location.txt').readLines()*.split(' +').collectEntries {
            [it[0], it[1]]
        }
    }
}


class UrlFile {
    static final GMT = TimeZone.getTimeZone('GMT')
    static final MODIFIED_PATTERN = 'EEE, dd MMM yyyy HH:mm:ss z'
    static String ifModifiedSince(Date d) { d.format(MODIFIED_PATTERN,GMT) }
    static Date parseLastModified(String s) { Date.parse(MODIFIED_PATTERN,s,GMT) }
    //
    final URL url
    final File dir
    final File localFile
    UrlFile(String url, String dir) {
        this.url = url.toURL()
        this.dir = new File(dir)
        this.dir.mkdirs()
        this.localFile = new File(this.dir,this.url.file)
    }
    String getText() {
        localFile.exists() ? localFile.text : remoteText
    }
    String getRemoteText() {
        HttpURLConnection conn = url.openConnection()
        conn.setRequestProperty 'If-Modified-Since', ifModifiedSince(new Date(localFile.lastModified()))
        conn.requestMethod = 'GET'
        if (conn.responseCode == 200) {
            String content = conn.inputStream.text
            localFile.write content
            localFile.lastModified = parseLastModified(conn.getHeaderField('Last-Modified')).time
            content
        }
    }
}
