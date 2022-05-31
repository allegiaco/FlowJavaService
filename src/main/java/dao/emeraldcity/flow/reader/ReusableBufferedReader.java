package dao.emeraldcity.flow.reader;


import java.io.IOException;
import java.io.Reader;

public class ReusableBufferedReader extends Reader {

    private char[] buffer = null;
    private int writeIndex = 0;
    private int readIndex = 0;
    private boolean endOfReaderReached = false;
    private int counter = 0;

    private boolean skipLF = false;
    private int nChars, nextChar;
    private static int defaultExpectedLineLength = 80;
    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0;

    private Reader source;

    public ReusableBufferedReader() {
        this.buffer = new char[8192];
        nextChar = nChars = 0;
    }

    public ReusableBufferedReader(char[] buffer) {
        this.buffer = buffer;
        nextChar = nChars = 0;
    }

    public ReusableBufferedReader setSource(Reader source) {
        this.source = source;
        this.writeIndex = 0;
        this.readIndex = 0;
        this.endOfReaderReached = false;
        this.nextChar = 0;
        this.nChars = 0;
        return this;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int charsRead = 0;
        int data = 0;
        while (data != -1 && charsRead < len) {
            data = read();
            if (data == -1) {
                endOfReaderReached = true;
                if (charsRead == 0) {
                    return -1;
                }
                return charsRead;
            }
            cbuf[off + charsRead] = (char) (65535 & data);
            charsRead++;
        }
        return charsRead;
    }

    @Override
    public int read() throws IOException {
        if (endOfReaderReached) {
            return -1;
        }

        if (readIndex == writeIndex) {
            if (writeIndex == buffer.length) {
                this.writeIndex = 0;
                this.readIndex = 0;
            }
            // data should be read into buffer.
            int bytesRead = readCharsIntoBuffer();
            while (bytesRead == 0) {
                // continue until you actually get some bytes !
                bytesRead = readCharsIntoBuffer();
            }

            // if no more data could be read in, return -1;
            if (bytesRead == -1) {
                return -1;
            }
        }

        return 65535 & this.buffer[readIndex++];
    }

    @Override
    public void close() throws IOException {

        this.source.close();

        for (int i = 0; i < nChars; i++) {
            buffer[i] = '\u0000';
        }
    }

    public String readLine() throws IOException {

        return readLine(false, null);
    }

    String readLine(boolean ignoreLF, boolean[] term) throws IOException {

        StringBuilder s = null;
        int startChar;

        synchronized (lock) {
            ensureOpen();
            boolean omitLF = ignoreLF || skipLF;
            if (term != null)
                term[0] = false;

            bufferLoop: for (;;) {

                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars) { /* EOF */
                    if (s != null && s.length() > 0)
                        return s.toString();
                    else
                        return null;
                }
                boolean eol = false;
                char c = 0;
                int i;

                /* Skip a leftover '\n', if necessary */
                if (omitLF && (buffer[nextChar] == '\n'))
                    nextChar++;
                skipLF = false;
                omitLF = false;

                charLoop: for (i = nextChar; i < nChars; i++) {
                    c = buffer[i];
                    if ((c == '\n') || (c == '\r')) {
                        if (term != null)
                            term[0] = true;
                        eol = true;
                        break charLoop;
                    }
                }

                startChar = nextChar;
                nextChar = i;

                if (eol) {
                    String str;
                    if (s == null) {
                        str = new String(buffer, startChar, i - startChar);
                    } else {
                        s.append(buffer, startChar, i - startChar);
                        str = s.toString();
                    }
                    nextChar++;
                    if (c == '\r') {
                        skipLF = true;
                    }
                    return str;
                }

                if (s == null)
                    s = new StringBuilder(defaultExpectedLineLength);
                s.append(buffer, startChar, i - startChar);
            }
        }
    }

    private void ensureOpen() throws IOException {
        if (source == null)
            throw new IOException("Stream closed");
    }

    private void fill() throws IOException {
        int dst;
        if (markedChar <= UNMARKED) {
            /* No mark */
            dst = 0;
        } else {
            /* Marked */
            int delta = nextChar - markedChar;
            if (delta >= readAheadLimit) {
                /* Gone past read-ahead limit: Invalidate mark */
                markedChar = INVALIDATED;
                readAheadLimit = 0;
                dst = 0;
            } else {
                if (readAheadLimit <= buffer.length) {
                    /* Shuffle in the current buffer */
                    System.arraycopy(buffer, markedChar, buffer, 0, delta);
                    markedChar = 0;
                    dst = delta;
                } else {
                    /* Reallocate buffer to accommodate read-ahead limit */
                    char ncb[] = new char[readAheadLimit];
                    System.arraycopy(buffer, markedChar, ncb, 0, delta);
                    buffer = ncb;
                    markedChar = 0;
                    dst = delta;
                }
                nextChar = nChars = delta;
            }
        }

        int n;
        do {
            n = source.read(buffer, dst, buffer.length - dst);
        } while (n == 0);
        if (n > 0) {
            nChars = dst + n;
            nextChar = dst;
        }
    }

    private int readCharsIntoBuffer() throws IOException {
        int charsRead = this.source.read(this.buffer, this.writeIndex, this.buffer.length - this.writeIndex);
        writeIndex += charsRead;
        return charsRead;
    }
}
