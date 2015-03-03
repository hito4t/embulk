/*
 * $Id: typical.epf 2627 2010-03-18 01:40:13Z tiba $
 */
package org.embulk.standards;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


public class PartialFileInputStream extends InputStream {
	
	private final InputStream original;
	private long start;
	private long end;
	private long current;
	private boolean eof;
	
	public PartialFileInputStream(InputStream original, long start, long end) {
		this.original = new BufferedInputStream(original);
		this.start = start;
		this.end = end;
		current = -1;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		/*
		initialize();
		
		if (current >= end) {
			return -1;
		}
		
		int read = original.read(b, 0, Math.min(b.length, (int)(end - current)));
		if (read >= 0) {
			current += read;
		}
		
		return read;
		*/
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		/*
		initialize();
		
		if (current >= end) {
			return -1;
		}

		int read = original.read(b, off, Math.min(len, (int)(end - current)));
		if (read >= 0) {
			current += read;
		}
		return read;
		*/
		
		/*
		for (int i = 0; i < len; i++) {
			int c = read();
			if (c < 0) {
				if (i == 0) {
					return -1;
				}
				return i;
			}
			b[off + i] = (byte)c;
		}
		return len;
		*/
		
		initialize();

		if (eof) {
			return -1;
		}
		
		int read = original.read(b, off, len);
		if (read < 0) {
			eof = true;
			return -1;
		}
		
		current += read;
		if (current >= end) {
			for (int i = Math.max((int)(end - current + read), 0); i < len; i++) {
				if (b[off + i] == '\n') {
					eof = true;
					return i;
				}
			}
		}
		
		return read;
	}

	@Override
	public int read() throws IOException {
		initialize();

		if (eof) {
			return -1;
		}
		
		int read = original.read();
		current++;
		
		if (read < 0) {
			eof = true;
			return -1;
		}
		
		if (current >= end) {
			if (read == '\n') {
				eof = true;
			}
		}
		return read;
	}
	
	@Override
	public long skip(long n) throws IOException {
		throw new IOException("Skip not supported.");
		/*
		long skip = original.skip(n);
		current += skip;
		return skip;
		*/
	}
	
	@Override
	public int available() throws IOException {
		return 0;
	}
	
	@Override
	public void close() throws IOException {
		original.close();
	}
	
	private void initialize() throws IOException {
		if (current >= start) {
			return;
			
		}
		if (start == 0) {
			current = 0;
		} else {
			current = original.skip(start);
			if (current != start) {
				throw new IOException("Cannot skip.");
			}
			
			int c;
			while ((c = original.read()) >= 0) {
				start++;
				current++;
				if (c == '\n') {
					break;
				}
			}
		}
		
		if (start >= end) {
			eof = true;
		}
	}
}
