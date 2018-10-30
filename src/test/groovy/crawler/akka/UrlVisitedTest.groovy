package crawler.akka

import akka.actor.ActorRef
import akka.actor.PoisonPill
import akka.pattern.PatternsCS
import akka.testkit.TestActor
import akka.testkit.TestActorRef
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

import java.util.concurrent.CompletableFuture

@Subject(UrlVisited)
class UrlVisitedTest extends ActorBaseTest {

    def conditions = new PollingConditions(initialDelay: 0.1, timeout: 1, factor: 1)
    
    def "should respond with poison pill when the url is already visited"() {
        given:
        TestActorRef<UrlVisited> ref = TestActorRef.create(system, UrlVisited.props(), "testA")
        ref.tell(new UrlVisited.AddVisited('akka1'), ActorRef.noSender())
        
        when:
        final CompletableFuture<Object> isNotVisited = PatternsCS.ask(ref, new UrlVisited.IsNotVisited('akka1'), 1000).toCompletableFuture()

        then:
        conditions.eventually {
            isNotVisited.isDone()
            assert isNotVisited.get() instanceof PoisonPill
        }
    }

    def "should respond with visited when the url was not visited before"() {
        given:
        TestActorRef<UrlVisited> ref = TestActorRef.create(system, UrlVisited.props(), "testB")
        ref.tell(new UrlVisited.AddVisited('akka1'), ActorRef.noSender())

        when:
        final CompletableFuture<Object> visit = PatternsCS.ask(ref, new UrlVisited.IsNotVisited('akka21'), 1000).toCompletableFuture()

        then:
        conditions.eventually {
            visit.isDone()
            def msg = visit.get()
            assert msg instanceof UrlVisited.Visit
            assert msg.url == 'akka21'
        }

    }
}
