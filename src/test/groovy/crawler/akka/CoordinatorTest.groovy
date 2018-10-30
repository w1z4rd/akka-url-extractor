package crawler.akka

import akka.actor.ActorRef
import akka.testkit.javadsl.TestKit
import crawler.util.HtmlParser
import spock.lang.Subject

@Subject(Coordinator)
class CoordinatorTest extends ActorBaseTest {

    def parser = Mock(HtmlParser)

    def "test sending message from coordinator"() {
        given:
        def testProbe = new TestKit(system)
        def testProbe1 = new TestKit(system)
        def urls = ['url1', 'url2', 'url1']
        parser.getUrls('akka1') >> urls
        parser.getUrls('url1') >> urls
        parser.getUrls('url2') >> urls

        def coordinator = system.actorOf(Coordinator.props(testProbe.getRef(), testProbe1.getRef(), parser))

        when:
        coordinator.tell(new Coordinator.EntryPoint('akka1'), ActorRef.noSender())

        then:
        def addUrl = testProbe.expectMsgClass(UrlAggregator.AddUrl)
        addUrl.url == 'akka1'

        def addVisited = testProbe1.expectMsgClass(UrlVisited.AddVisited)
        addVisited.url == 'akka1'

        def collect = urls.collect({
            testProbe.expectMsgClass(UrlAggregator.AddUrl).url
        })

        collect.sort() == urls.sort()
    }

}