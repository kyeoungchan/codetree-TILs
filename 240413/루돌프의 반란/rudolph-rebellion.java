//package codetree.루돌프의반란;

import java.util.*;
import java.io.*;

/**
 * 1~p까지 산타들이 크리스마스 이브를 준비하는 중 루돌프가 반란을 일으킴. 루돌프를 잡아야한다.
 * 게임: N x N 격자
 * 최상단: 1,1
 * 게임은 M개의 턴
 * 루돌프와 산타들이 한 번씩 움직인다.
 * 루돌프가 먼저 움직이고, 1번부터 p번까지의 산타들이 순서대로 움직인다.
 * 산타들이 움직일 수 없는 조건
 * 1. 기절
 * 2. 격자밖으로 빠져나간 경우 => 게임에서 탈락함
 * 거리: 제곱수로 표현
 * <p>
 * 루돌프의 움직임: 가장 가까운 산타를 향해 1칸 돌진한다.(탈락하지 않은 산타로 선택)
 * 거리가 같은 산타가 여러명인 경우, r이 큰 산타가 먼저, 그다음 c가 큰 산타가 먼저
 * 루돌프는 8방향으로 돌진가능하다.
 * <p>
 * 산타의 움직임
 * 탈락한 산타는 움직일 수 없다.
 * - 루돌프에게 거리가 가장 가까워지는 방향으로 1칸 이동한다.
 * - 다른 산타가 있는 칸이나 게임판 밖으로는 움직이지 않는다.
 * - 움직일 수 있는 칸이 없다면 산타는 움직이지 않는다.
 * - 움직일 수 있는 칸이 있더라도 루돌프랑 가까워지지 않는다면 산타는 움직이지 않는다.
 * - 산타는 4방향으로 움직이고, 방향 우선순위는 상우하좌다.
 * <p>
 * 충돌
 * - 루돌프가 움직여서 충돌이 일어난 경우: C만큼 점수
 * - 루돌프가 이동한 방향으로 C칸만큼 밀려난다.
 * - 산타가 움직여서 충돌이 일어난 경우: D만큼 점수
 * - 자신이 이동한 반대 방향으로 D만큼 밀려난다.
 * - 게임판 밖으로 나가면 산타는 게임에서 탈락
 * - 다른 산타를 만나면 상호작용
 * <p>
 * 상호작용
 * - 다른 산타가 있다면 그 산타는 1칸 해당 방향으로 밀려난다.
 * - 연쇄적으로 상호작용 가능
 * <p>
 * 기절
 * - 현재 k -> k+1은 기절 상태
 * - 기절한 산타는 움직일 수 없고, 기절 도중 충돌이나 상호작용으로 인해 밀려날 수는 있다.
 * - 기절한 산타도 돌진 대상이 될 수 있다.
 * <p>
 * 게임 종료
 * - M번의 턴이 모두 거치거나, P명의 산타가 모두 탈락하면 게임 종료
 * - 매턴 이후 아직 탈락하지 않은 산타들에게 1점씩 추가로 부여
 */
public class Main {

	static final int INF = 10_000;
	static int N, P, C, D, rr, rc, map[][], santas[];
	static int[] dri = {-1, -1, 0, 1, 1, 1, 0, -1}, drj = {0, 1, 1, 1, 0, -1, -1, -1}, dsi = {-1, 0, 1, 0}, dsj = {0, 1, 0, -1};
	static boolean[] loosed;
	static int[] sleeping, scores;


	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = new StringTokenizer(br.readLine(), " ");
		N = Integer.parseInt(st.nextToken()); // 1 ~ N
		int M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine(), " ");
		rr = Integer.parseInt(st.nextToken()) - 1;
		rc = Integer.parseInt(st.nextToken()) - 1;
		santas = new int[P + 1];
		loosed = new boolean[P + 1];
		sleeping = new int[P + 1];
		scores = new int[P + 1];
		map = new int[N][N];
		for (int i = 0; i < P; i++) {
			st = new StringTokenizer(br.readLine(), " ");
			int number = Integer.parseInt(st.nextToken()); // 산타 번호
			int r = Integer.parseInt(st.nextToken()) - 1;
			int c = Integer.parseInt(st.nextToken()) - 1;
			map[r][c] = number;
			santas[number] = r * N + c;
		}

		for (int i = 0; i < M; i++) {
			updateSleeping();
			rudolphMove();
			santasMove();
			if (!giveScores()) {
				break;
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int s = 1; s < P + 1; s++) {
			sb.append(scores[s]).append(" ");
		}
		System.out.println(sb.toString());
		br.close();
	}

	static void updateSleeping() {
		for (int s = 1; s < P + 1; s++) {
			if (loosed[s]) continue;
			if (sleeping[s] > 0) sleeping[s]--;
		}
//		System.out.println("updateSleeping");
//		System.out.println(Arrays.toString(sleeping));
//		System.out.println();
	}

	static void rudolphMove() {
//		System.out.println("rudolphMove start");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("rr = " + rr);
//		System.out.println("rc = " + rc);
		int minDist = INF;
		int target = 0;
		for (int s = 1; s < P + 1; s++) {
			if (loosed[s]) continue;
			int sr = santas[s] / N;
			int sc = santas[s] % N;
			int dist = getDistance(sr, sc, rr, rc);
			if (minDist > dist) {
				minDist = dist;
				target = s;
			} else if (minDist == dist) {
				int sr2 = santas[target] / N;
				int sc2 = santas[target] % N;
				if (sr > sr2) {
					target = s;
				} else if (sr == sr2 && sc > sc2) {
					target = s;
				}
			}
		}
//		System.out.println("final target");
//		System.out.println("target = " + target);
//		System.out.println("minDist = " + minDist);
		int sr = santas[target] / N;
		int sc = santas[target] % N;
		int d = getDirection(sr, sc);
//		System.out.println("d = " + d);
		if (minDist == 2 || minDist == 1) {
			kick(target, sr, sc, d);
		}
		rr += dri[d];
		rc += drj[d];
//		System.out.println("after rudolph moved");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("rr = " + rr);
//		System.out.println("rc = " + rc);
//		System.out.println();
	}

	static int getDirection(int sr, int sc) {
		int min = INF;
		int finalD = 0;
		for (int d = 0; d < 8; d++) {
			int nr = rr + dri[d];
			int nc = rc + drj[d];
			if (nr < 0 || nr > N - 1 || nc < 0 || nc > N - 1) continue;
			int dist = getDistance(nr, nc, sr, sc);
			if (min > dist) {
				min = dist;
				finalD = d;
			}
		}
		return finalD;
	}
	static void kicked(int santa, int sr, int sc, int d) {
		map[sr][sc] = 0;
		sr = rr;
		sc = rc;
		d = (d + 2) % 4;
		int nsi = sr + dsi[d] * D;
		int nsj = sc + dsj[d] * D;
		scores[santa] += D;
//		System.out.println("before Kicked");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("sr = " + sr);
//		System.out.println("sc = " + sc);
//		System.out.println("d = " + d);
//		System.out.println("D = " + D);
//		System.out.println("nsi = " + nsi);
//		System.out.println("nsj = " + nsj);
//		System.out.println("map[nsi][nsj] = " + map[nsi][nsj]);

		if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
			// 탈락한 경우
			loosed[santa] = true;
			santas[santa] = -1;
			sleeping[santa] = -1;
		} else if (map[nsi][nsj] != 0) {
			// 자리에 다른 산타가 있다면
//			System.out.println("met another santa!");
			int a = santa;
			boolean wentOut = false;
			while (map[nsi][nsj] != 0) {
				// 연쇄 상호작용
				int b = map[nsi][nsj];
				map[nsi][nsj] = a;
				santas[a] = nsi * N + nsj;
				nsi += dsi[d];
				nsj += dsj[d];
				if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
					loosed[b] = true;
					santas[b] = -1;
					wentOut = true;
					break;
				}
				a = b;
			}
			if (!wentOut) {
				map[nsi][nsj] = a;
				santas[a] = nsi * N + nsj;
			}
		} else {
			// 빈 칸이면 그냥 거기에 산타를 놓는다.
			map[nsi][nsj] = santa;
			santas[santa] = nsi * N + nsj;
		}
		if (!loosed[santa]) sleeping[santa] = 2;
//		System.out.println("after Kicked");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
	}

	static void kick(int santa, int sr, int sc, int d) {
//		System.out.println("before Kick");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println("sr = " + sr);
//		System.out.println("sc = " + sc);
//		System.out.println("d = " + d);
//		System.out.println("C = " + C);
		int nsi = sr + dri[d] * C;
		int nsj = sc + drj[d] * C;
		map[sr][sc] = 0;
		if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
			// 탈락한 경우
			loosed[santa] = true;
			sleeping[santa] = -1;
			santas[santa] = -1; // 디버깅을 위한 세팅
		} else if (map[nsi][nsj] != 0) {
			// 자리에 다른 산타가 있다면
			int a = santa;
			boolean wentOut = false;
			while (map[nsi][nsj] != 0) {
				// 연쇄 상호작용
				int b = map[nsi][nsj];
				map[nsi][nsj] = a;
				santas[a] = nsi * N + nsj;
				nsi += dri[d];
				nsj += drj[d];
				if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
					loosed[b] = true;
					santas[b] = -1;
					wentOut = true;
					break;
				}
				a = b;
			}
			if (!wentOut) {
				map[nsi][nsj] = a;
				santas[a] = nsi * N + nsj;
			}
		} else {
			// 빈 칸이면 그냥 거기에 산타를 놓는다.
			map[nsi][nsj] = santa;
			santas[santa] = nsi * N + nsj;
		}
		if (!loosed[santa])
			sleeping[santa] = 2;
		scores[santa] += C;
//		System.out.println("after Kick");
//		for (int i = 0; i < N; i++) {
//			for (int j = 0; j < N; j++) {
//				System.out.print(map[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
	}

	static int getDistance(int r1, int c1, int r2, int c2) {
		return (int) (Math.pow(r1 - r2, 2) + Math.pow(c1 - c2, 2));
	}

	static void santasMove() {
//		System.out.println("santasMove start!");
		boolean metRudolph, canMove;
		int minDist, finalD;
		for (int s = 1; s < P + 1; s++) {
			// 탈락했거나 기절한 산타는 움직이지 않는다.
			if (loosed[s] || sleeping[s] != 0) continue;
//			System.out.println(s + " santa Move start!");
//			System.out.println("beforeMoving");
//			for (int i = 0; i < N; i++) {
//				for (int j = 0; j < N; j++) {
//					System.out.print(map[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
			int sr = santas[s] / N;
			int sc = santas[s] % N;

			metRudolph = false;
			canMove = false;
			minDist = getDistance(sr, sc, rr, rc);
			finalD = 0;
			for (int d = 0; d < 4; d++) {
				int ni = sr + dsi[d];
				int nj = sc + dsj[d];
				if (ni < 0 || ni > N - 1 || nj < 0 || nj > N - 1 || map[ni][nj] != 0) continue;
				if (ni == rr && nj == rc) {
					metRudolph = true;
					finalD = d;
					break;
				}
				int dist = getDistance(rr, rc, ni, nj);
				if (minDist > dist) {
					canMove = true;
					minDist = dist;
					finalD = d;
				}
			}
			if (metRudolph) {
				map[sr][sc] = 0;
				kicked(s, sr, sc, finalD);
				continue;
			}
			if (!canMove) {
				// 한 번도 움직인 적 없으면 움직이지 않고 다음 산타
//				System.out.println("cannot Move!");
				continue;
			}

			map[sr][sc] = 0;
			int nr = sr + dsi[finalD];
			int nc = sc + dsj[finalD];
			map[nr][nc] = s;
			santas[s] = nr * N + nc;
//			System.out.println("after Move without kicking");
//			for (int i = 0; i < N; i++) {
//				for (int j = 0; j < N; j++) {
//					System.out.print(map[i][j] + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
		}
//		System.out.println("santas moving End!");
	}

	static boolean giveScores() {
		// 모든 산타가 탈락했다면 게임 종료를 하기 위해 false 반환
//		System.out.println("giveScores Start!");
		boolean alive = false;
		for (int s = 1; s < P + 1; s++) {
			if (loosed[s]) continue;
			scores[s]++;
			alive = true;
		}
//		System.out.println(Arrays.toString(scores));
		return alive;
	}


}