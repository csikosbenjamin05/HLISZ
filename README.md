# 3D Fizikai Szimulációs Motor és Modellező Szoftver (Java / Processing 4)

Kutatási Projekt & Szoftverarchitektúra – TUDOK XXIV. Kárpát-medencei Konferencia, Kutatási terv tömb I. szekció: I. helyezés 2024.

Ez a projekt egy nulláról felépített, két rétegű, többszálú 3D-s fizikai szimulációs keretrendszer, amely a közegellenállás (szabadesés, ferde hajítás, műholdak keringése) valós idejű modellezésére készült. A szoftver egyedisége, hogy a Processing 4 alapjaira építkezve egy saját fejlesztésű grafikus felhasználói felületet (UI), aszinkron erőforráskezelőt és komplex, többszálú fizikai szimulációt valósít meg. A szimuláció pontosságát valós kísérletekkel (pl. különböző labdák tízemeletes épületből történő leejtésével) validáltuk, tizedesjegy-pontosságú egyezést elérve a mért és a számított adatok között.



### Szabadesés ( videó )
[![Szabadesés](https://img.youtube.com/vi/wlFaJQsYqsk/maxresdefault.jpg)](https://youtu.be/wlFaJQsYqsk)



## 1. Technikai felépítés és Főbb modulok

A szoftver robusztus, moduláris architektúrával rendelkezik, amely elválasztja a logikát, a megjelenítést és az adatkezelést.

* **Fizikai Motor (`base3D.bodies` és `base3D.bodies.forces`):** A fizikai rendszert a komponens-alapú tervezés jellemzi. A testek (pl. `Sphere`, `Cube`, `Mesh`) egy `PhysicsComponent` objektumot tartalmaznak, amely a sebesség és gyorsulás Euler-módszeren alapuló integrációját végzi a valós eltelt idő (`DeltaTime`) alapján. Az erőhatások (`Force` interfész) dinamikusan adhatók hozzá a testekhez, mint például a lineáris gravitáció (`LinearGravity`), a centrális gravitáció (`SphericalGravity`) vagy a sebességfüggő közegellenállás (`Drag`).


* **Egyedi UI Keretrendszer (`base3D.ui`):** A beépített megoldások helyett egy saját, reszponzív UI rendszert implementáltam. Az `Area`, `UIVector` és a `Coordinate` (relatív és abszolút koordinátákat kezelő) osztályok segítségével a felületek automatikusan igazodnak az ablakméret változásaihoz. A modul interaktív elemeket (gombok, csúszkák, jelölőnégyzetek, feliratok) tartalmaz.


* **Erőforráskezelő (`base3D.resources`):** A `ResourceManager` felelős a textúrák, hangok és 3D modellek memóriatakarékos és aszinkron betöltéséért. A rendszer egy XML konfigurációs fájlból (`resources.xml`) olvassa ki a szükséges elemeket, kiküszöbölve a redundáns fájlbetöltéseket és biztosítva a memóriaszivárgás-mentes működést.


### Ferde hajítás
[![Ferde hajítás](https://img.youtube.com/vi/Mpk_MQRIKak/maxresdefault.jpg)](https://youtu.be/Mpk_MQRIKak)



## 2. A Többszálú működés kialakítása

A szoftver egyik legnagyobb technikai kihívása és erőssége az egyedi szálkezelés, amelyet a `ThreadController` fog össze. A teljesítmény maximalizálása és a fő szál (renderelés) tehermentesítése érdekében a rendszer több szálon operál:

* **Fizikai és Logikai Szál (`ObjectUpdateThread`):** A fizikai számítások egy dedikált szálon futnak (ideálisan magas, pl. 240 UPS - Updates Per Second sebességgel), függetlenül a képernyő frissítési rátájától (FPS). A szálbiztosság (thread-safety) elérésére a motor "Double Buffering" mechanizmust használ. Két lista létezik: a `backObjects` (ahol a kalkulációk történnek) és a `frontObjects` (amelyet a renderelő szál olvas). A frissítési ciklus végén egy `merge()` metódus szinkronizálja az adatokat, megakadályozva a *ConcurrentModificationException*-t és az adatintegritási hibákat.


* **UI Szál (`UIUpdateThread`):** A felhasználói felület logikája és eseménykezelése külön szálon fut (pl. 30 UPS), így a UI nem lassítja a fizikai szimulációt.


* **Taszk Szálak (`TaskThread`):** A nehéz műveletek – mint a komplex 3D hálók (Mesh) generálása (`ShapeGeneratorThread`), vagy a textúrák fájlrendszerből történő beolvasása (`ResourceLoaderThread`) – háttérszálakon történnek aszinkron módon, kiküszöbölve az alkalmazás megakadását.


### Testek keringése
[![Testek keringése](https://img.youtube.com/vi/fKBzLqXPoYg/maxresdefault.jpg)](https://youtu.be/fKBzLqXPoYg)



## 3. Felhasznált Tervezési Minták

A kód minőségét és skálázhatóságát szoftvertervezési minták tudatos alkalmazása biztosítja:

* **Singleton (Egyke):** Globálisan elérhető, egyetlen példányban létező menedzserekhez alkalmazva (pl. `ThreadController`, `ResourceManager`, `EngineProject`, `EventHandler`), amelyek a rendszer magját alkotják.


* **Observer (Megfigyelő / Pub-Sub):** Az `EventHandler` osztály ezen a mintán alapul. A billentyűzet és az egér eseményeire dinamikusan fel lehet iratkozni (subscribe), így a kód lazán csatolt marad. Ugyanez a minta jelenik meg a textúrák aszinkron betöltésekor: az objektumok feliratkoznak a textúrára, és automatikusan újragenerálják magukat, ha az betöltött (`resourceUpdated` callback).


* **Strategy (Stratégia):** A `Force` interfész lehetővé teszi, hogy a fizikai motor futásidőben dinamikusan cserélje vagy halmozza az erőhatásokat (pl. `Drag`, `LinearGravity`) a testeken, anélkül, hogy a test osztályát módosítani kellene.


* **Factory / Builder (Építő):** A `ShapeGenerator` leszármazottak (pl. `MeshShapeGenerator`, `SphereShapeGenerator`) felelősek a komplex 3D alakzatok paraméterezhető legyártásáért.


### Keringés csökkenő sebességgel
[![Keringés csökkenő sebességgel](https://img.youtube.com/vi/SoBM0i-LxDQ/maxresdefault.jpg)](https://youtu.be/SoBM0i-LxDQ)
---

*Személyes megjegyzés a CV-hez: A projekt során Flóring Balázs a fizikai modellek kidolgozásáért és a valós mérésekért felelt, míg én (Csíkos Benjamin) a szimulációs program architektúráját, a fizikai motor programozását és a grafikus megjelenítést terveztem és kódoltam.*
