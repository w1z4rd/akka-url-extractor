package crawler.akka

import akka.actor.ActorRef
import akka.testkit.TestActorRef
import spock.lang.Subject

@Subject(UrlAggregator)
class UrlAggregatorTest extends ActorBaseTest {

    UrlAggregator actor

    def "test sending message"() {
        given:
        TestActorRef<UrlAggregator> ref = TestActorRef.create(system, UrlAggregator.props(), "testA")
        actor = ref.underlyingActor()

        when:
        ref.tell(new UrlAggregator.AddUrl('akka1'), ActorRef.noSender())
        ref.tell(new UrlAggregator.AddUrl('akka1'), ActorRef.noSender())

        then:
        actor.urls == ['akka1': 2]
    }
}