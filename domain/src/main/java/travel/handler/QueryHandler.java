package travel.handler;

import travel.query.Query;
import travel.result.Result;

public interface QueryHandler<R extends Result, T extends Query> {
    R handle(T query);
}
