pega-proof-of-concept-proxy
================================

Proxy microservice proof of concept for future PEGA work, submits a string to PEGA-API

## Public API

| Path                                                            | Description                                  |
|-----------------------------------------------------------------|----------------------------------------------|
| [POST /pega-proof-of-concept-proxy/submit-payload](#post-submit-payload) | Submits payload to pega proof of concept API |

## POST /submit-payload
Submits payload to pega proof of concept API.

**Example request**

```json
{
    "data": "exampleString"
}
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").