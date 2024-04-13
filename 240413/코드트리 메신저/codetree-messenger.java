import java.util.*;
import java.io.*;


public class Main {

    static int[] parents, authority;
    static boolean[] isOff, v;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");
        StringBuilder sb = new StringBuilder();
        int N = Integer.parseInt(st.nextToken());
        int Q = Integer.parseInt(st.nextToken());
        parents = new int[N + 1];
        authority = new int[N + 1];
        isOff = new boolean[N + 1];
        for (int cm = 0; cm < Q; cm++) {
            st = new StringTokenizer(br.readLine(), " ");
            int c;
            int command = Integer.parseInt(st.nextToken());
            switch (command) {
                case 100:
                    for (int i = 1; i < N + 1; i++) {
                        parents[i] = Integer.parseInt(st.nextToken());
                    }
                    for (int i = 1; i < N + 1; i++) {
                        authority[i] = Integer.parseInt(st.nextToken());
                    }
                    break;
                case 200:
                    c = Integer.parseInt(st.nextToken());
                    isOff[c] = !isOff[c];
                    break;
                case 300:
                    c = Integer.parseInt(st.nextToken());
                    int power = Integer.parseInt(st.nextToken());
                    authority[c] = power;
                    break;
                case 400:
                    int c1 = Integer.parseInt(st.nextToken());
                    int c2 = Integer.parseInt(st.nextToken());
                    int temp = parents[c1];
                    parents[c1] = parents[c2];
                    parents[c2] = temp;
                    break;
                case 500:
                    c = Integer.parseInt(st.nextToken());
                    v = new boolean[N + 1];
                    v[c] = true;
                    int result = 0;
                    for (int num = N; num > 0; num--) {
                        if (v[num] || isOff[num]) continue;
                        v[num] = true;
                        result += dfs(num, authority[num], c);
                    }
                    sb.append(result).append("\n");
                    break;
            }
//            debug(command);
        }
        System.out.println(sb.toString());
        br.close();
    }

    static int dfs(int num, int cnt, int target) {
        if (num == target) {
            return 1;
        }
        int result = 0;
        int parent = parents[num];

        // 위에가 root거나 본인이 off면 부모 호출을 할 수가 없으므로 0 리턴
        if (parent == 0 || isOff[parent]) return result;

        if (cnt > 0) {
            result += dfs(parent, cnt - 1, target);
        }
        if (!v[parent]) {
            v[parent] = true;
            result += dfs(parent, authority[parent], target);
        }
        return result;
    }

    static void debug(int command) {
        System.out.println(command + "명령 결과");
        System.out.println(Arrays.toString(parents));
        System.out.println(Arrays.toString(authority));
        System.out.println(Arrays.toString(isOff));
    }
}