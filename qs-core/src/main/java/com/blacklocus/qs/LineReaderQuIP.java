/**
 * Copyright 2013 BlackLocus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blacklocus.qs;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * {@link QueueItemProvider} based on lines of text from an InputStream.
 */
public class LineReaderQuIP implements QueueItemProvider<String>, Closeable {

    private final BufferedReader reader;
    private final int limit;

    /**
     * Volatile because {@link #hasNext()} may be checked by another thread.
     */
    private volatile boolean itsOn;
    private int count = 0;

    /**
     * @param inputStream newline-separated JSON documents
     * @param limit       (optional) maximum number of JSON documents (lines) to read. Defaults to unlimited.
     */
    public LineReaderQuIP(InputStream inputStream, @Nullable Integer limit) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.limit = limit == null ? Integer.MAX_VALUE : limit;
        this.itsOn = true;
    }

    @Override
    public Iterator<Collection<String>> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        // Because this returns Collections in next(), we can afford to return a few empty collections at the end.
        // hasNext is "eventually consistent". Not peeking ahead simplifies our logic significantly.
        return itsOn;
    }

    @Override
    public Collection<String> next() {
        String line;
        if ((line = readLine()) != null && count++ < limit) {
            return Collections.singletonList(line);
        } else {
            itsOn = false;
            return Collections.emptyList();
        }
    }

    String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
