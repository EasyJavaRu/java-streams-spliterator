package ru.easyjava.java;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MultilineString {
    private final String data[];

    public MultilineString(String source) {
        data = source.split("\n");
    }

    Stream<String> stream() {
        return StreamSupport.stream(new MultilineSpliterator(), false);
    }

    Stream<String> parallelStream() {
        return StreamSupport.stream(new MultilineSpliterator(), true);
    }

    public class MultilineSpliterator implements Spliterator<String> {
        private int firstPosition, lastPosition;

        public MultilineSpliterator() {
            firstPosition = 0;
            lastPosition = data.length-1;
        }

        public MultilineSpliterator(int f, int l) {
            firstPosition = f;
            lastPosition = l;
        }

        @Override
        public boolean tryAdvance(Consumer<? super String> action) {
            if (firstPosition <= lastPosition) {
                firstPosition++;
                action.accept(data[firstPosition]);
                return true;
            }

            return false;
        }

        @Override
        public void forEachRemaining(Consumer<? super String> action) {
            for (;firstPosition <= lastPosition; firstPosition++) {
                action.accept(data[firstPosition]);
            }
        }

        @Override
        public Spliterator<String> trySplit() {
            int half = (lastPosition - firstPosition)/2;
            if (half<=1) {
                //Not enough data to split
                return null;
            }
            int f = firstPosition;
            int l = firstPosition + half;

            firstPosition = firstPosition + half +1;

            return new MultilineSpliterator(f, l);
        }

        @Override
        public long estimateSize() {
            return lastPosition- firstPosition;
        }

        @Override
        public long getExactSizeIfKnown() {
            return estimateSize();
        }

        @Override
        public int characteristics() {
            return IMMUTABLE | SIZED | SUBSIZED;
        }
    }
}
