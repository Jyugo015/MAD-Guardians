package com.example.madguardians.firebase;

import android.util.Log;

import com.example.madguardians.utilities.FirebaseController;
import com.example.madguardians.utilities.UploadCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MediaFB {
    private static String TABLE_NAME = FirebaseController.MEDIA;
    private String mediaId;
    private String mediaSetId;
    private String url;
    private static Queue<HashMap<String, Object>> insertQueue = new LinkedList<>();
    private static Queue<HashMap<String, Object>> insertMediaSetQueue = new LinkedList<>();
    private static Queue<UploadCallback<String>> callbackQueue = new LinkedList<>();

    private static UploadCallback<String> mediaIdCallback = null;

    private MediaFB(String mediaId, String mediaSetId, String url) {
        this.mediaId = mediaId;
        this.mediaSetId = mediaSetId;
        this.url = url;
    }

    public String getMediaId() {
        return mediaId;
    }

    public String getMediaSetId() {
        return mediaSetId;
    }

    public String getUrl() {
        return url;
    }


    public static void initialiseMedia() {
        ArrayList<HashMap<String, Object>> hashMapList = new ArrayList<>();
        // Germany 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/a1-german-online-course_image_hzllo1.jpg"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00001", "https://res.cloudinary.com/dmgpozfee/image/upload/german-grammar-3_la1wxo.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00002", "https://res.cloudinary.com/dmgpozfee/video/upload/videogerman_lvyllv.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00003", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736193680/ml7rwphc4hxples8qosz"));
        // Germany 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00004", "https://res.cloudinary.com/dmgpozfee/image/upload/images_1_aq5u45.png"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00004", "https://res.cloudinary.com/dmgpozfee/image/upload/Examples-German-Verbs_n4t7wa.webp"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00005", "https://res.cloudinary.com/dmgpozfee/video/upload/videoGerman2_gl00ik.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00006", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736193909/w3cplllnbwvsndbbpari"));
        // Germany 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00007", "https://res.cloudinary.com/dmgpozfee/image/upload/images_1_aq5u45.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00008", "https://res.cloudinary.com/dmgpozfee/video/upload/videoGerman_op8ndh.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00009", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736193940/ainlxev5cj4m6uhuhubq"));
        // French 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00010", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736181234/french-grammar-7_vb7xys.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00011", "https://res.cloudinary.com/dmgpozfee/video/upload/videoplayback_zwhp1j.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00012", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736208624/sjpfqgr33tblvku3ovl2"));
        // French 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00013", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736181245/images_oa8htx.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00014", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736181235/videoplayback_1_lmnreb.mp4"));
        // French 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00016", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190637/images_r8bx3l.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00017", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736181251/videoplayback_2_wzjgr9.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00018", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736208699/dueas81luhdvlxujn7zm"));
        // Java 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00019", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190764/programmingTerms_lbj1y7.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00020", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736190766/videoplayback_nvyjep.mp4"));
        // Java 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00021", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190777/images_bnwcu8.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00022", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736190775/videoplayback_1_jywzbd.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00023", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209072/cydxmdi09vfnf70wocxr"));
        // Java 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00024", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190785/java-program-structure_ztnbuw.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00025", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736190788/videoplayback_2_b9re6t.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00026", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209104/qqy5xb47kqlgqkxmxnu5"));
        // Python 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00027", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190918/awan_7_tips_python_beginners_8_res8vh.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00028", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736190923/videoplayback_porhsq.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00029", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209315/iqcos70myqk6qeywcmd5"));
        // Python 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00030", "https://res.cloudinary.com/dmgpozfee/image/upload/images_1_aq5u45.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00031", "https://res.cloudinary.com/dmgpozfee/video/upload/videoGerman_op8ndh.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00032", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209255/wbwjc3ukbtfxjgwt1lnt"));
        // Python 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00033", "https://res.cloudinary.com/dmgpozfee/image/upload/python_h7q69z.pdf"));
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00034", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736190940/649daafb6eb94436150fa768d652a677f4bcf087_hlr6zh.png"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00035", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209394/gglon3wxwrzihfgjagsf"));
        // Light 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00036", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191299/Color-Wheel-Backwards-for-Pigments_trgzbq.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00037", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191302/videoplayback_jo8dql.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00038", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209462/gg0um5sjl9yxdb0qeyh7"));
        // Light 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00039", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191313/images_fhnajj.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00040", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191317/videoplayback_1_lflslu.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00041", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209488/q57esoiwi5fzconubja0"));
        // Light 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00042", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191322/stock-vector-vector-diagram-with-the-visible-light-spectrum-visible-light-infr_eamxsw.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00043", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191325/videoplayback_2_bamcjz.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00044", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209516/s3zboefmek8ixvjthb2g"));
        // Gravity 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00045", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191432/ITV_Image_map_ExploringPhysicsConceptsWithA_GravityWell_BG-PLATE_FINAL_18Jun19_ln6qw2.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00046", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191435/videoplayback_unuy4b.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00047", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209618/zgswglxk85etsn9ppvm4"));
        // Gravity 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00048", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191443/043221-65fcaf4b-a7ab-4aa2-895c-e2dfddfad8da_illqkc.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00049", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191447/videoplayback_1_szk5qz.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00050", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209645/uflpspj8idunaitbgpym"));
        // Gravity 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00051", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191461/newtonian-gravity_n9i2eu.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00052", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191460/videoplayback_2_ab9qch.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00053", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209670/y7v3pb3tgnmdjimxqsh6"));
        // Chemical Equilibrium 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00054", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191676/Chemical-Equilibrium-Factors-Affecting-Chemical-Equilibrium-01_ahsynl.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00055", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191680/videoplayback_fuds3n.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00056", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209754/l2iy31cyzkkejlf1mq7i"));
        // Chemical Equilibrium 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00057", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191685/Chemical-Reaction-Equilibrium_b0xjob.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00058", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191690/videoplayback_1_n1nezv.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00059", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209780/bjix68lrmt9hxji88ehb"));
        // Chemical Equilibrium 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00060", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191740/Chemical-Equilibrium-Factors-Affecting-Chemical-Equilibrium-_k2aumt.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00061", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191744/videoplayback_2_xttyzr.mp4"));
        // Nuclear 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00062", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191852/Nuclear-Reaction-3-700x423_d4hbpu.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00063", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191844/videoplayback_wzvs5m.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00064", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209891/auxsrf6ckga2aezjvdut"));
        // Nuclear 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00065", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191868/CNX_Chem_21_03_RadioDecay_xvdvix.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00066", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191865/videoplayback_1_ib1gu8.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00067", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209931/eyoc8vazd0y2c3vtvsxw"));
        // Nuclear 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00068", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191878/apha_radiation_prwqaz.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00069", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191882/videoplayback_2_bjy9al.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00070", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736209960/dnyktvo4c9rgpvyt0waf"));
        // Plant 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00071", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736191988/biology-11-01782-g001_ybpfef.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00072", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736191992/videoplayback_ftpfty.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00073", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210107/rc1wrav4yffkoj0t2qtt"));
        // Plant 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00074", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192000/44316_2024_1_Figa_HTML_izmpvx.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00075", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192003/videoplayback_1_t4rbc4.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00076", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210178/sbnrgsc73igffxtiwueg"));
        // Plant 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00077", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192010/1-s2.0-S0958166922001914-ga1_cfqxbk.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00078", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192013/videoplayback_2_cha1bn.mp4"));
        // Cellular Biology 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00079", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192100/animalcell_odei1t.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00080", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192124/videoplayback_1_wmgomq.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00081", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210355/eoga08hhtzz415egeq06"));
        // Cellular Biology 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00082", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192126/BSB-Article-Cells-scaled_ppt9fv.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00083", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192247/videoplayback_2_gmob61.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00084", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210387/ffhhxtigigtiw6htl8ji"));
        // Cellular Biology 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00085", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192159/cells-animal-plant-ways-nucleus-difference-organelles_xdfb4a.webp"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00086", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192252/videoplayback_1_fbdc2x.mp4"));
        // Algebra 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00087", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192395/algebric-expression-image-3-1615010733_yg82pm.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00088", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192398/videoplayback_uhlanb.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00089", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210505/ufbd6rotpqeepoewpidw"));
        // Algebra 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00090", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192409/Basic-Of-Algebra_msen65.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00091", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192407/videoplayback_1_af6pxe.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00092", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210529/d4pxw8bk5my7tlay7spb"));
        // Algebra 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00093", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192417/maths-formula-chart-3-_1_bngaa3.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00094", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192421/videoplayback_2_u4ciul.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00095", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210559/clzh1w1o4xf5hlrgfv2z"));
        // Statistic 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00096", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192533/Screenshot-2024-03-26-151544_tc4bqw.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00097", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192537/videoplayback_tnsw38.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00098", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210718/un9set6npgg3wofoslti"));
        // Statistic 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00099", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192543/shapes_jocknj.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00100", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192547/videoplayback_1_zjcpgz.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00101", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210765/jhspy8hsojphcyrcbcuf"));
        // Statistic 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00102", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192552/Untitled-Diagram-918_p10rqp.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00103", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192557/videoplayback_2_eg98iy.mp4"));
        // Piano 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00104", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192648/60d24e134b12f15b003a2aee_PC_2_h9c73n.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00105", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192645/videoplayback_zkdgvf.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00106", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210853/i7ivkwjhj9eh3pcfe2cd"));
        // Piano 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00107", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192656/19a03bd603ad61dc9294fbbac5271ce0_blmtgw.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00108", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192660/videoplayback_1_rwumfo.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00109", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210892/srfxse1trldbqayjqhxi"));
        // Piano 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00110", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192668/images_cg8bop.png"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00111", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192671/videoplayback_2_jgwdsg.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00112", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736210946/zdxh8wcffpud0ei67kes"));
        // Guitar 1
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00113", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192783/images_mqxqjt.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00114", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192787/videoplayback_d3pa3w.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00115", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736211060/uavzfjivl4eryabhu6cm"));
        // Guitar 2
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00116", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192796/C-Major-guitar-chord-58b973a15f9b58af5c489177_fhewal.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00117", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192794/Beginner_Guitar_Lesson_Starter_Pack_lgyoj4.mp4"));
        hashMapList.add(createMediaData(FirebaseController.PDF, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00118", "https://res.cloudinary.com/dmgpozfee/raw/upload/v1736211193/lsoqjkxbyjjcumqr2au7"));
        // Guitar 3
        hashMapList.add(createMediaData(FirebaseController.IMAGE, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00119", "https://res.cloudinary.com/dmgpozfee/image/upload/v1736192802/12-beginner-guitar-chords_okmw9r.jpg"));
        hashMapList.add(createMediaData(FirebaseController.VIDEO, FirebaseController.findStarting(FirebaseController.MEDIASET) + "00120", "https://res.cloudinary.com/dmgpozfee/video/upload/v1736192806/Memorize_the_Fretboard_in_3_MINUTES_zudiqr.mp4"));

        for (HashMap<String, Object> dataHashMap:hashMapList) {
            insertMedia(dataHashMap, new UploadCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TABLE_NAME, "onSuccess:  initialise media");
                }
                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    public static void insertMedia(HashMap<String, Object> data, UploadCallback<String> mediaIdCallback) {
        insertQueue.add(data);
        callbackQueue.add(mediaIdCallback);
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertQueue.size() == 1) {
            processQueue();
        }
    }

    private static void processQueue() {
        if (!insertQueue.isEmpty()) {
            HashMap<String, Object> dataHashMap = insertQueue.peek();
            mediaIdCallback = callbackQueue.peek();
            String tableName = (String) dataHashMap.remove("tableName");
            Log.d(TABLE_NAME, "processQueue: tableName: "+ tableName);
            FirebaseController.generateDocumentId(tableName, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    Log.d(TABLE_NAME, "processQueue: mediaId: "+ id);
                    FirebaseController.insertFirebase(TABLE_NAME, id, dataHashMap, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            Log.d("processQueue media", "onSuccess");
                            insertQueue.poll();
                            callbackQueue.poll();
                            if((Boolean) dataHashMap.get("isLast") != null && (Boolean) dataHashMap.get("isLast")) mediaIdCallback.onSuccess(id);
                            processQueue();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Log.e("processQueue media", "onFailure", e);
                            insertQueue.poll();
                            callbackQueue.poll();
                            processQueue();
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {mediaIdCallback.onFailure(e);}
            });
        }
    }

    public static HashMap<String, Object> createMediaData(String tableName, String mediaSetId, String url) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("tableName", tableName);
        data.put("mediaSetId", mediaSetId);
        data.put("url", url);
        return data;
    }

    public static void initialiseMediaSetId() {
        for (int i = 1; i <= 120; i++) {
            insertMediaSet();
        }
    }

    public static void insertMediaSet() {
        insertMediaSetQueue.add(new HashMap<String, Object>());
        // start for the first, after that the method will call by itself, making sure no repetitive calling
        if (insertMediaSetQueue.size() == 1) {
            processMediaSetQueue();
        }
    }

    private static void processMediaSetQueue() {
        if (! insertMediaSetQueue.isEmpty()) {
            FirebaseController.generateDocumentId(FirebaseController.MEDIASET, new UploadCallback<String>() {
                @Override
                public void onSuccess(String id) {
                    Log.d("TAG", "onSuccess: mediaSetId" + id);
                    HashMap<String, Object> mediaSetId = new HashMap<>();
                    mediaSetId.put("mediaSetId", id);
                    FirebaseController.insertFirebase(FirebaseController.MEDIASET, id, mediaSetId, new UploadCallback<HashMap<String, Object>>() {
                        @Override
                        public void onSuccess(HashMap<String, Object> result) {
                            insertMediaSetQueue.poll();
                            processMediaSetQueue();
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("TAG", "onFailure: ", e);
                }
            });
        }
    }

    public static void getMedias(String mediaSetId, UploadCallback<List<MediaFB>> callback) {
        Log.d(TABLE_NAME, "mediaSetId: " + mediaSetId);
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(FirebaseController.MEDIASET), mediaSetId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> mediaHashMapList) {
                ArrayList<MediaFB> medias = new ArrayList<>();
                for (HashMap<String, Object> mediaHashMap:mediaHashMapList) {
                    medias.add(mapHashMapToMedia(mediaHashMap));
                }
                callback.onSuccess(medias);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    private static MediaFB mapHashMapToMedia(HashMap<String, Object> media) {
        return new MediaFB((String) media.get("mediaId"), (String) media.get("mediaSetId"), (String) media.get("url"));
    }

    public static void getMedia(String mediaId, UploadCallback<MediaFB> callback) {
        FirebaseController.getMatchedCollection(TABLE_NAME, FirebaseController.getIdName(TABLE_NAME), mediaId, new UploadCallback<List<HashMap<String, Object>>>(){
            @Override
            public void onSuccess(List<HashMap<String, Object>> result) {
                MediaFB media = mapHashMapToMedia(result.get(0));
                callback.onSuccess(media);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
}
