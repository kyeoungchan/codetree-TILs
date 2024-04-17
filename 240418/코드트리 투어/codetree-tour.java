import java.io.*;
import java.util.*;

public class Main {

    static class Item implements Comparable<Item> {
        int id;
        int revenue;
        int dest;
        int priority;

//        - 선택 조건: 이 상품을 판매함으로써 얻게 되는 이득(revenue - cost)가 최대인 상품을 우선적으로 고려
        public Item(int id, int revenue, int dest) {
            this.id = id;
            this.revenue = revenue;
            this.dest = dest;
            this.priority = revenue - dist[dest];
        }

        public boolean canSell() {
            return !(dist[dest] == INF || priority < 0);
        }

        /* - 이득이 같은 상품이 여러 개면 id가 가장 작은 상품 선택
         * - cost: 출발지로부터 상품의 도착지까지 도달하기 위한 최단거리
         */
        @Override
        public int compareTo(Item o) {
            return priority == o.priority ? Integer.compare(id, o.id) : Integer.compare(o.priority, priority);
        }
    }

    static final int INF = 987654321;
    static int origin;
    static int[] dist;
    static List<int[]>[] edges;
    static TreeSet<Item> ts;
    static HashMap<Integer, Boolean> removed;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        int Q = Integer.parseInt(br.readLine());
        origin = 0;
        ts = new TreeSet<>();
        removed = new HashMap<>();
        int id;
        for (int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            int command = Integer.parseInt(st.nextToken());
            switch (command) {
                case 100:
                    int N = Integer.parseInt(st.nextToken());
                    int M = Integer.parseInt(st.nextToken());
                    init(N, M, st);
                    break;
                case 200:
                    /*
                     * 2. 여행상품 생성
                     * 200 id revenue dest
                     * id, revenue(상품 매출), dest(상품도착지)
                     * 주어지는 id는 모두 다르다. => 같은 id의 상품을 입력으로 주어지지 않는다.
                     */
                    id = Integer.parseInt(st.nextToken());
                    int revenue = Integer.parseInt(st.nextToken());
                    int dest = Integer.parseInt(st.nextToken());
                    insert(id, revenue, dest);
                    break;
                case 300:
                    /* * 3. 여행상품 취소
                     * 300 id
                     * id에 해당하는 여행상품이 존재하는 경우, 해당 id의 여행 상품을 관리 목록에서 삭제
                     */
                    id = Integer.parseInt(st.nextToken());
                    remove(id);
                    break;
                case 400:
                    sell();
                    break;
                case 500:
                    /* 5. 여행 상품의 출발지 변경
                     * 500 s
                     * - 여행 상품의 출발지를 전부 s로 변경하는 명령이다.
                     * - 출발지가 변경됨에 따라 각 상품의 cost도 변경된다.
                     */
                    int s = Integer.parseInt(st.nextToken());
                    changeOrigin(s);
                    break;
            }
        }
        br.close();
    }

    static void changeOrigin(int s) {
        origin = s;
        dijkstra();
        List<Item> tempList = new ArrayList<>();
        while (!ts.isEmpty()) {
            Item item = ts.pollFirst();
            if (removed.get(item.id)) continue; // 제거된 상품이면 건너뛴다.
            tempList.add(new Item(item.id, item.revenue, item.dest)); // 각 item 정보들마다 새롭게 업데이트한다.
        }
        ts.addAll(tempList);
    }

    static void sell() {
        /* 4. 최적의 여행 상품 판매
         * - 판매가 완료되면 해당 상품의 id를 출력하고, 관리목록에서 제거한다.
         * - 판매 가능한 상품이 전혀 없다면 -1을 출력하고 아무런 상품도 제거하지 않는다.
         */

        /* 판매하지 않는 조건
         * - 도착지에 도달이 불가능
         * - cost > revenue
         */
        if (ts.isEmpty() || !ts.first().canSell()) {
            System.out.println(-1);
            return;
        }
        while (!ts.isEmpty() && ts.first().canSell()) {
            // 팔 수 있는 물건이라면 일단 꺼낸다.
            Item item = ts.pollFirst();
            if (removed.get(item.id)) continue; // 이미 삭제된 상품이라면 건너뛴다.
            // 여기까지 왔다면 판매할 수 있는 상품들 중에서 제일 최적인 상품이다.
            System.out.println(item.id);
            return;
        }
        // 삭제된 상품들을 모두 꺼내고 보니 판매할 상품이 없는 경우
        System.out.println(-1);
    }
    static void remove(int id) {
        if (!removed.containsKey(id)) return;
        removed.put(id, true);
    }

    static void insert(int id, int revenue, int dest) {
        Item newItem = new Item(id, revenue, dest);
        ts.add(newItem);
        removed.put(id, false);
    }

    static void debug() {
        System.out.println(Arrays.toString(dist));
    }
    static void init(int N, int M, StringTokenizer st) {
        /*
         * n개의 도시와 m개의 간선
         * 도시는 0~n-1 번호가 있다.
         * 간선에는 방향성 X
         * 출발지는 항상 하나로 통일, 처음에는 0번 도시가 출발지
         * 1. 코드트리 랜드 건설
         * 100 n m ...
         * 도시의 수n, 간선의 수m
         * 간선에 대항하는 정보(v, u, w) 도시v, 도시u, 가중치w 연결
         */

        edges = new List[N];
        for (int i = 0; i < N; i++) {
            edges[i] = new ArrayList<>();
        }

        for (int i = 0; i < M; i++) {
            int a = Integer.parseInt(st.nextToken());
            int b = Integer.parseInt(st.nextToken());
            int dist = Integer.parseInt(st.nextToken());
            if (a == b) continue;
//          자기 자신을 향하는 간선도 존재할 수 있다. => 최소 거리와는 무관하므로 건너뛴다.
            edges[a].add(new int[]{b, dist});
            edges[b].add(new int[]{a, dist});
        }

        dist = new int[N];
        dijkstra();
    }

    static void dijkstra() {
        Arrays.fill(dist, INF);
        dist[origin] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((o1, o2) -> Integer.compare(o1[1], o2[1]));
        pq.offer(new int[]{origin, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int minVertex = cur[0];
            int min = cur[1];
            if (dist[minVertex] < min) continue;
            for (int[] edge : edges[minVertex]) {
                int nextV = edge[0];
                int d = edge[1];
                if (dist[nextV] > min + d) {
                    dist[nextV] = min + d;
                    pq.offer(new int[]{nextV, dist[nextV]});
                }
            }
        }
    }
}