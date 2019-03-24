package com.molt3nrock.baseball;

import com.molt3nrock.collection.DistinctList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BaseBallGame {

  private static final int DIGITS_LENGTH = 3;
  static final int MAX_STRIKES = DIGITS_LENGTH;
  private static BufferedReader br;

  public static void main(String[] args) {
    br = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * 컴퓨터의 랜덤숫자 생성.
   *
   * 사용자가 맞춰야 할 {@code GameDigit}으로 이루어진 중복이 없는 {@value DIGITS_LENGTH}자리의 숫자를 생성하는 함수 입니다.
   *
   * @return 중복이 없는 {@code GameDigit} 리스트.
   *
   * @see GameDigit
   */
  static DistinctList<GameDigit> generateStrikeZone() {
    assert (GameDigit.MAX <= 9);
    assert (GameDigit.MIN >= 1);
    assert (GameDigit.MAX > GameDigit.MIN);
    assert (GameDigit.MAX - GameDigit.MIN + 1 >= DIGITS_LENGTH);
    ArrayList<Integer> validDigits = IntStream
        .range(GameDigit.MIN, 1 + GameDigit.MAX)
        .boxed()
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(validDigits);
    Stream<GameDigit> digitStream = validDigits
        .stream()
        .map(GameDigit::new)
        .limit(DIGITS_LENGTH);
    return new DistinctList<>(digitStream);
  }

  /**
   * 사용자의 입력.
   *
   * 사용자에게서 {@code parseUserInput}를 이용해 올바른 입력 받을 때까지 무한히 입력 루프를 반복합니다.
   *
   * @return {@code Distinct<GameDigit>}
   *
   * @throws IOException 게임 입력을 위하여{@code BufferedReader}를 사용하고 출력을 위하여 {@code System.out}을
   *                     사용하므로 입출력 오류시 {@code IOException}이 발생할 수 있습니다.
   * @see #parseUserInput
   * @see DistinctList
   * @see GameDigit
   */
  private static DistinctList<GameDigit> pitching() throws IOException {
    DistinctList<GameDigit> pitch;
    while (true) {
      System.out.print("숫자를 입력해주세요:");
      pitch = parseUserInput(br.readLine());
      if (pitch.size() == DIGITS_LENGTH) {
        break;
      } else {
        System.out.println("중복 되지 않는 1부터 9까지의 숫자로 구성된 3자리 자연수를 입력해주세요.");
      }
    }
    return pitch;
  }

  /**
   * 사용자 입력 파싱.
   *
   * 사용자가 입력한 문자열을 {@value DIGITS_LENGTH}자리의 {@code DistinctList<GameDigit>}으로 변환 합니다.
   *
   * @param input 사용자의 입력 문자열
   *
   * @return {@code Distinct<GameDigit>} 단, 변환이 불가능한 입력이 주어질 경우 Distinct.empty()를 반환합니다.
   *
   * @see GameDigit
   * @see DistinctList
   * @see DistinctList#empty
   */
  static DistinctList<GameDigit> parseUserInput(String input) {
    if (input.length() != DIGITS_LENGTH) {
      return DistinctList.empty();
    }
    Stream<GameDigit> digitStream = input
        .chars()
        .mapToObj(Character::getNumericValue)
        .filter(c -> c >= GameDigit.MIN && c <= GameDigit.MAX)
        .map(GameDigit::new);
    DistinctList<GameDigit> distinctDigits = DistinctList.of(digitStream);
    if (distinctDigits.size() == DIGITS_LENGTH) {
      return distinctDigits;
    } else {
      return DistinctList.empty();
    }
  }

  /**
   * 사용자의 숫자 리스트와 컴퓨터의 숫자 리스트 비교.
   *
   * 컴퓨터가 생성한 임의의 {@code Distinct<GameDigit>}와 사용자가 입력한 문자열을 파싱하여 생성한
   * {@code Distinct<GameDigit>}를 비교하여 {@code Hint}를 리턴합니다.
   *
   * @param strikeZone 컴퓨터가 생성한 중복이 없는 숫자 리스트.
   * @param pitch      사용자의 입력을 파싱해 생성한 중복이 없는 숫자 리스트.
   *
   * @return {@code strikeZone}와 {@code pitch} 비교결과인 스트라이크와 볼 상태를 담고 있습니다.
   *
   * @see Hint
   */
  static Hint umpireDecides(DistinctList<GameDigit> strikeZone, DistinctList<GameDigit> pitch) {
    assert (strikeZone.size() == DIGITS_LENGTH);
    assert (pitch.size() == DIGITS_LENGTH);
    long strikes = IntStream
        .range(0, DIGITS_LENGTH)
        .filter(i -> strikeZone.get(i).equals(pitch.get(i)))
        .count();
    long uniques = strikeZone.concat(pitch).count();
    long duplicates = DIGITS_LENGTH * 2 - uniques;

    long balls = duplicates - strikes;
    if (strikes == 0 && balls == 0 && strikeZone.isConsecutive()) {
      balls = MAX_STRIKES + 1;
    }
    return new Hint((int) strikes, (int) balls);
  }
}
