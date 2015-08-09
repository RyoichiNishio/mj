#麻雀のロジックについて。

まずゲームを完成させてしまった後に、AIを作るべき。
可能なすべての局面において、可能なactionをすべて列挙する必要がある。

Game : 半荘戦とか東風戦とかの、1ゲームを表すとする。
Position : 南2局2本場、などのような、ある1局(配牌から誰かのあがりあるいは流局まで)を表すとする。
Phase : あるPositionにおいて、playerがactionを選択する場面を指す。Phaseには二つある。
一つは、一人のplayerが打牌を選択する場面。もう一つは、3人のplayersが仕掛けるかどうかを判断する場面。
Clearing : 流局あるいは誰かが上がった際に、清算を行う。

Gameの流れ。

1. playerの順番と起親を設定する。4人をランダムに並び替えて、0番目を親とすればよい。
2. Positionを開始する。
3. Action-PhaseとReaction-Phaseを繰り返す。
4. Clearingを行う。
5. 次のPositionを開始し、南4局終了まで繰り返す。
6. 点数が多い順に順位を決定して終了。

Action-Phase
あるplayerが、次に行うactionを決定する
1. TileをDiscardする -> Reaction-Phaseへ。もしこのAction-Phaseがミンカン直後のAction-Phaseなら、カンドラをめくってからReaction-Phaseへ。
2. リーチ宣言する。-> 再びAction-Phaseへ。
3. ツモあがりする。 -> Clearingへ。
4. アンカンする。 -> カンドラめくり&リンシャンツモし、Action-Phaseへ。もしこのAction-Phaseがミンカン直後のAction-Phaseなら、カンドラをめくってからReaction-Phaseへ。
5. カカンする。 -> Reaction-Phaseへ。もしこのAction-Phaseがミンカン直後のAction-Phaseなら、カンドラをめくってからReaction-Phaseへ。

Reaction-Phase
3人のplayerが、それぞれ、ロンや鳴きなどのActionをするかどうかを決定する。
1. 誰かがロンをする -> Clearingへ。3人なら流局。(トリロン)
2. 誰かがダイミンカンする ->　リンシャンツモしてAction-Phaseへ。リーチ宣言直後なら、リーチ成立。
3. 誰かがポンする -> Action-Phaseへ。リーチ宣言直後なら、リーチ成立。
4. 誰かがチーする -> Action-Phaseへ。リーチ宣言直後なら、リーチ成立。
5. 全員がスルーする -> リーチ宣言直後なら、リーチ成立。流局判定する。(残り山数=0, 4カン, スーフー連打 -> Clearing それ以外、次の人がツモってAction-Phaseへ。)

ちゃんと、可能なAction, 可能でないActionのすべてが判定できるような情報を、各Phaseが持っていないといけない。
さらに、可能なActionをとったあと、何が行われるか(カンドラをめくるとか、リーチが成立して供託+1000となるとか)も完全に判定できる必要がある。

class Phase
    val publicState : PublicState
    val playerStates : List[PlayerState]
    val latentState : LatentState

class PublicState
    val gameType :GameType = 東風 or 半荘
    val scores : Seq[Int] = 4playersの点数
    val position :Position = (南, 3, 2)
    val deposit : Int = 供託に出されているリーチ棒の本数
    val river : Seq[???] : 各playerのactionの配列
    val turnPlayer : Int = ActionPhaseなら、打牌選択するplayer。ReactionPhaseなら、打牌選択していたplayer
    val context = 型は未定
    val doraIndicatingTiles : Seq[Int]
class PlayerState
    private val hand : Seq[Tile]
    public val meldedSets : Seq[meldedSet]
        meldedSetは、鳴いたTileとHandから出たTiles, そして誰から鳴いたかの情報が必要である。単にSetの種類ではダメ。
    public val status = (closed(面前), melded, reach-declared, reach-realized)
    private val isFuriten : Boolean
    public val waitingTiles = (13枚の際には、役なしであれ、待ちの牌のリストを計算しておく。)

class LatentState
    val wall
    val edge
    public val remaingWall = wall.length

class ActionPhase extends Phase
    val context = ( ダイミンカンあるいはカカン直後, ポンあるいはチーの直後, ツモの直後, アンカンの直後)
        食い変え判定が必要である。もし、contextがポンあるいはチーの直後である場合、そのplayerのplayerStateのmeldedSets.lastを見て、食い変えになる牌を判定する。
        context自体に、meldedSetの詳細情報を付与する必要はない。
    val actions = (ツモ、アンカン、リーチ宣言、流局、カカン、打)
class ReactionPhase extends Phase
    val context = ( カカン、河 )

---

#Positionの流れ



##1.ActionPhaseの場合。Phase.player.possibleActionsによって、可能なactionをすべて列挙する。
- ツモできるか
    contextはダイミンカンあるいはカカン直後,ツモの直後,アンカンの直後でないといけない
    turnPlayer(自分)のstatusがreach-declaredではダメ。
    handとmeldedSetsからあがり形かどうかの判定
    あがり形の場合、役がつくかどうかを判定
    手役がつくかどうか以外に、面前ツモか。嶺上開花か。天和、地和か。(全playerのmeldedSets.length==0であることと、wall.length>= 76が条件。親なら天和。)
    ハイテイか。(wall.length==0が条件)
- 流局できるか(九種)
    全playerのmeldedSets.length==0であることと、wall.length>= 76が条件。そしてhandの状況。
- リーチ宣言できるか。
    contextはツモの直後あるいはアンカンの直後
    statusはclosedの場合のみ
    テンパイでないといけない
    1000点以上あるか。
    wall.length>=4という条件
- アンカンできるか
    contextはダイミンカンあるいはカカンの直後、ツモの直後、アンカンの直後である必要あり
    statusがreach-declaredではダメ
    5回目のカンはできない。全員のmeldedSetsから数える。(doraIndicatingTiles.lengthはダメ。まだ4回目のカンドラがめくれていない可能性あるから。)
    statusがreach-realizedである場合、アンカンできない場合がある。
    アンカンできるのは、ツモった牌でカンする場合のみ。さらにこの場合、あがり牌が変わったり、あがり形の可能な分割数に変化があってはならない。
    もちろんhandに4枚ないといけない。
    wall.length>=1という条件あり。
    アンカン候補は複数ありえる。
- カカンできるか。
    アンカンできる条件と同じ。
- 打牌できるか
    statusがreach-declaredの場合、テンパイする牌のみ選択可能。
    statusがreach-realizedの場合、ツモ切りのみ可能。
    statusがポンあるいはチーの直後の場合、食い変えはダメ。

actionを選択した場合の処理
- ツモの場合
   Clearingへ。
- 流局の場合
   Clearingへ。
- リーチ宣言の場合
   river.add(pid, reach-declaration)
   statusをreach-declaredにして、再びActionPhaseへ。contextは変更しない。
- アンカンの場合
   river.add(pid, ankan(tile,tile,tile,tile))
   contextがダイミンカンあるいはカカンの直後の場合、カンドラをめくる。
   handとmeldedSetsを変更。
   全員のmeldedSetsから何回目のカンかどうかを計算する。(nとする。)
   カンドラめくる。(doraIndicatingTiles = latentState.ridge(4+2n) +: doraIndicatingTiles)
　 リンシャンツモを行う。 ridge(n-1)がリンシャンツモ。wall.lastを削除し、ridge(n-1)に置き換える。
   contextはアンカン直後に変更し、再びActionPhase
- カカンの場合
   river.add(pid, kakan(tile))
   contextがダイミンカンあるいはカカンの直後の場合、カンドラをめくる。
   handとmeldedSetsを変更。
   context:カカン直後のReactionPhaseへ。
- 河への打牌の場合
   river.add(pid, d(tile))
   contextがダイミンカンあるいはカカンの直後の場合、カンドラをめくる。
   handを変更。
   context:河のReactionPhaseへ。
   フリテンかどうかの計算を行う。テンパイしていなければフリテンでない。テンパイしている場合、あたり牌が自分の捨て牌(鳴かれた牌)に含まれていればフリテンとなる。
   これはriverから計算する。そうでない場合、フリテンではないので、isFuriten=falseに変更する。waitingHandsの再計算も行う。

##2. ReactionPhaseの場合。3人のplayerそれぞれに対して、可能なactionをすべて列挙する。
- ロンできるか
    handとmeldedSetsがテンパイ形かつ、打牌があたり牌か
    手役がつくか。チャンカンになるか。レンホーになるか。ホウテイになるか。
    isFuriten=falseでなければならない。
- ダイミンカンできるか
    contextは河とする(一応)
    アンコを持っている
    5回目のカンはできない。
    statusはclosedかmeldedである必要がある
    wall.length>=1でないとダメ
- ポンできるか
    contextは河
    2マイ以上もっている
    statusはclosedかmeldedである
    wall.length>=1
- チーできるか
    contextは河
    チーできるターツがある。turnPlayerがカミチャである。
    statusはclosedかmeldedである
    wall.length>=1
    チーした場合に、食い変えにしかならない場合はチーできない
スルーできるか
    常に可能


3人のreactionのsetに関する処理
全員スルー
    context:カカンの場合、
    　あがれるのにスルーしたplayerを、フリテンにする。(あがれるとは、ロンできるかどうかとは別。テンパイ時の待ちであれば。なので、全playerのあたり牌を保持しておくとよい。)
      turnPlayerがリンシャンツモ。
      turnPlayerのActionPhaseへ。contextはダイミンカンあるいはカカン直後。
    context:河の場合
    　　turn-playerがreach-declaredの場合、reach-realizedに変更し、1000点をdepositに移動する。
    　　流局判定する。スーフーレンダ、スーチャリーチ、カン４回目。wall.length == 0 => 流局へ。
    　　流局とならない場合、trunPlayer+=1として、context:ツモのActionPhaseへ。wall.firstをplayer.handへ。wallからwall.firstを削除する。
    　　あがれるのにスルーしたplayerを、フリテンにする。
3人ロンの場合
    clearingへ。トリロンで流局。
1,2人ロンの場合
    clearingへ。ダブロンは採用。
ダイミンカン成立の場合
    あがれるのにスルーしたplayerを、フリテンにする。
    turn-playerがreach-declaredの場合、reach-realizedに変更し、1000点をdepositに移動する。
    ダイミンカンのplayerをturnPlayerとし、リンシャンツモし、context:ダイミンカンあるいはカカン直後のActionPhaseとする。
    river.add(pid, daiminkan(tile,tile,tile,tile))
ポンあるいはチー成立の場合
    あがれるのにスルーしたplayerを、フリテンにする。
    turn-playerがreach-declaredの場合、reach-realizedに変更し、1000点をdepositに移動する。
    ポンのplayerをturnPlayerとし、context:ポンあるいはチー直後のActionPhaseとする。
　　river.add(pid,ponかchi(tile,tile,tile))




