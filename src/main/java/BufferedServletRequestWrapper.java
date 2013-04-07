package gsst;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/*
 * Original Authors: (Special Thanks to...)
 * http://blog.honestyworks.jp/blog/archives/162   (real original author?)
 * http://d.hatena.ne.jp/machi_pon/20090120/1232420325
 * http://ameblo.jp/vashpia77/entry-10826082231.html
 */
public class BufferedServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] buffer;

    public BufferedServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream is = request.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buff[] = new byte[1024];
        int read;
        while((read = is.read(buff)) > 0) {
            baos.write(buff, 0, read);
        }
        this.buffer = baos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new BufferedServletInputStream(this.buffer);
    }
}
