import java.util.*;
import java.io.*;

public class Main {

	static final int INF = 10_000;
	static int N, P, C, D, rr, rc, map[][], santas[];
	static int[] dri = {-1, -1, 0, 1, 1, 1, 0, -1}, drj = {0, 1, 1, 1, 0, -1, -1, -1}, dsi = {-1, 0, 1, 0}, dsj = {0, 1, 0, -1};
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
			if (sleeping[s] == -1) continue;
			else if (sleeping[s] > 0) sleeping[s]--;
		}
	}

	static void rudolphMove() {
		int minDist = INF;
		int target = 0;
		for (int s = 1; s < P + 1; s++) {
			if (sleeping[s] == -1) continue;
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
		int sr = santas[target] / N;
		int sc = santas[target] % N;
		int d = getDirection(sr, sc);
		if (minDist == 2 || minDist == 1) {
			kick(target, sr, sc, d);
		}
		rr += dri[d];
		rc += drj[d];
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

		if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
			// 탈락한 경우
			sleeping[santa] = -1;
		} else if (map[nsi][nsj] != 0) {
			// 자리에 다른 산타가 있다면
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
					sleeping[b] = -1;
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
		if (sleeping[santa] != -1) sleeping[santa] = 2;
	}

	static void kick(int santa, int sr, int sc, int d) {
		int nsi = sr + dri[d] * C;
		int nsj = sc + drj[d] * C;
		map[sr][sc] = 0;
		if (nsi < 0 || nsi > N - 1 || nsj < 0 || nsj > N - 1) {
			// 탈락한 경우
			sleeping[santa] = -1;
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
					sleeping[b] = -1;
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
		if (sleeping[santa] != -1)
			sleeping[santa] = 2;
		scores[santa] += C;
	}

	static int getDistance(int r1, int c1, int r2, int c2) {
		return (int) (Math.pow(r1 - r2, 2) + Math.pow(c1 - c2, 2));
	}

	static void santasMove() {
		boolean metRudolph, canMove;
		int minDist, finalD;
		for (int s = 1; s < P + 1; s++) {
			// 탈락했거나 기절한 산타는 움직이지 않는다.
			if (sleeping[s] != 0) continue;
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
				continue;
			}

			map[sr][sc] = 0;
			int nr = sr + dsi[finalD];
			int nc = sc + dsj[finalD];
			map[nr][nc] = s;
			santas[s] = nr * N + nc;
		}
	}

	static boolean giveScores() {
		// 모든 산타가 탈락했다면 게임 종료를 하기 위해 false 반환
		boolean alive = false;
		for (int s = 1; s < P + 1; s++) {
			if (sleeping[s] == -1) continue;
			scores[s]++;
			alive = true;
		}
		return alive;
	}


}