## Ytdl-Api

### Routes:

- `GET /search/?limit=<limit>&query=<query>` \
  Search on YouTube for videos \
  `<limit>`: The maximum number of search results to return \
  `<query>`: Search query

- `GET /formats?id=<url>&format=<format>` \
  Return download URLs for the given video \
  `<url>`: YouTube video URL \
  `<format>`: The format to search for (Can be one of `mp3`, `mp4`, `wav`)

- `GET /download?id=<url>&format=<format>` \
  Download the video from YouTube \
  `<url>`: YouTube video URL \
  `<format>`: The format to download (Can be one of `mp3`, `mp4`, `wav`)

