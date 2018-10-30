package crawler.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.japi.pf.ReceiveBuilder;
import crawler.util.HtmlParser;

public class Coordinator extends AbstractActor {

    private final ActorRef aggregator;
    private final ActorRef visited;
    private final HtmlParser htmlParser;

    public Coordinator(ActorRef aggregator, ActorRef visited, HtmlParser htmlParser) {
        this.aggregator = aggregator;
        this.visited = visited;
        this.htmlParser = htmlParser;
    }

    public static class EntryPoint {

        private final String url;

        public EntryPoint(String url) {
            this.url = url;
        }
    }

    public static Props props(ActorRef aggregator, ActorRef visited, HtmlParser htmlParser) {
        return Props.create(Coordinator.class, aggregator, visited, htmlParser);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(EntryPoint.class, this::entryPoint)
                .match(Terminated.class, this::terminateSelf)
                .build();
    }

    private void terminateSelf(Terminated message) {
        getContext().unwatch(message.getActor());
        if (!getContext().getChildren().iterator().hasNext()) {
            aggregator.tell(new UrlAggregator.WriteOutput(), getSelf());
            getSelf().tell(PoisonPill.getInstance(), getSelf());
        }
    }

    private void entryPoint(EntryPoint message) {
        aggregator.tell(new UrlAggregator.AddUrl(message.url), getSelf());
        visited.tell(new UrlVisited.AddVisited(message.url), getSelf());
        htmlParser.getUrls(message.url)
                .parallelStream()
                .peek(url -> aggregator.tell(new UrlAggregator.AddUrl(url), getSelf()))
                .forEach(url -> {
                    ActorRef ref = getContext().actorOf(UrlExtractor.props(visited, aggregator, htmlParser));
                    getContext().watch(ref);
                    ref.tell(new UrlExtractor.ParseUrl(url), getSelf());
                });
    }

}
