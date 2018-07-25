grails.project.work.dir = 'target'

grails.project.repos.default = 'airon'
grails.project.dependency.distribution = {
    remoteRepository(id: 'airon', url: 'http://aironbrasil.com.br/nexus/content/repositories/releases') {
        authentication username: 'deployment', password: 'deployment123'
    }
}

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'
    repositories {
        mavenLocal()
        grailsCentral()
        mavenCentral()
    }
    plugins {
        build(':release:3.1.2', ':rest-client-builder:2.1.1') {
            export = false
        }
    }
}
