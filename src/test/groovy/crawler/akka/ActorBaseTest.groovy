package crawler.akka

import akka.actor.ActorSystem
import akka.testkit.javadsl.TestKit
import spock.lang.Specification

class ActorBaseTest extends Specification {

    static ActorSystem system

    def setupSpec() {
        system = ActorSystem.create()
    }

    def cleanupSpec() {
        TestKit.shutdownActorSystem(system)
        system = null
    }
}
