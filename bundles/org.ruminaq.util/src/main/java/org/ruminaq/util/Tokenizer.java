/*
 * (C) Copyright 2018 Marek Jagielski.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ruminaq.util;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

  private class TokenInfo {
    public final Pattern regex;
    public final int token;

    public TokenInfo(Pattern regex, int token) {
      super();
      this.regex = regex;
      this.token = token;
    }
  }

  public class Token {
    public final int token;
    public final String sequence;

    public Token(int token, String sequence) {
      super();
      this.token = token;
      this.sequence = sequence;
    }

  }

  private LinkedList<TokenInfo> tokenInfos = new LinkedList<>();
  private LinkedList<Token> tokens = new LinkedList<>();

  public void add(String regex, int token) {
    tokenInfos.add(new TokenInfo(Pattern.compile(regex), token));
  }

  public void tokenize(String str) {
    String s = str.trim();
    tokens.clear();
    while (!s.equals("")) {
      for (TokenInfo info : tokenInfos) {
        Matcher m = info.regex.matcher(s);
        if (m.find()) {
          String tok = m.group().trim();
          s = m.replaceFirst("").trim();
          tokens.add(new Token(info.token, tok));
          break;
        }
      }
    }
  }

  public LinkedList<Token> getTokens() {
    return tokens;
  }
}
