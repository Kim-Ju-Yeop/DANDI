module.exports = (req, res, next) => {
  const { protocol } = req; // protocol(http)
  const hostHeaderIndex = req.rawHeaders.indexOf('Host') + 1;
  const host = hostHeaderIndex ? req.rawHeaders[hostHeaderIndex] : undefined;
  Object.defineProperty(req, 'origin', {
    get: () => {
      if (!host) {
        if (!req.headers.referer) {
          return undefined;
        }
        return req.headers.referer.substring(0, req.headers.referer.length - 1);
      }
      return `${protocol}://${host}`;
    },
  });
  next();
};
