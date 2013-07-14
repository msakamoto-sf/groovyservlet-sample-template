package gsst;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/*
 * Original Authors: (Special Thanks to...)
 * http://blog.honestyworks.jp/blog/archives/162   (real original author?)
 * http://d.hatena.ne.jp/machi_pon/20090120/1232420325
 * http://ameblo.jp/vashpia77/entry-10826082231.html
 */
public class BufferedServletInputStream extends ServletInputStream {

    private ByteArrayInputStream is;

    public BufferedServletInputStream(byte[] buffer) {
        this.is = new ByteArrayInputStream(buffer);
    }

    @Override
    public int available() throws IOException {
        return is.available();
    }

    @Override
    public int read() throws IOException {
        return is.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return is.read(b, off, len);
    }

    @Override
    public void reset() throws IOException {
        is.reset();
    }
}
