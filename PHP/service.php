<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

//PARA DEBUG
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

$servername ="localhost";
$dbUsername = "teachersfriend";
$dbPassword = "userpwd";
$maindb = "main";
$mailHost = "smtp.gmail.com";
$mailUsername = "teachersfriendapp@gmail.com";
$mailPassword = "teachersfriendPDM2020";
$mailPort = "587";

$conn = mysqli_connect($servername, $dbUsername, $dbPassword);

if (!$conn) {
    $responseObject->success = false;
    $responseObject->error = "Unable to connect to MYSQL";
    $responseObject->debug = "Nº ".mysqli_connect_errno()." : ".mysqli_connect_error();
    $json = json_encode($responseObject);
    echo $json;
    exit();
}
if (!mysqli_set_charset($conn, "utf8")) {
    $responseObject->success = false;
    $responseObject->error = "Error loading character set utf8";
    $responseObject->debug = "Nº ".mysqli_error($conn);
    $json = json_encode($responseObject);
    echo $json;
    exit();
}

mysqli_select_db($conn,$maindb);

//if an admin does not exist create one

$firstquery = "SELECT COUNT(u_nome) as cnt FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id );";
$result = mysqli_query($conn,$firstquery);
if($result){
    if($row = mysqli_fetch_array($result, MYSQLI_ASSOC)){
        if($row['cnt'] == 0){
            $pwd = 'admin';
            $pwd_hashed = hash('md5',$pwd);
            mysqli_begin_transaction($conn);
            try{
                $sql = "INSERT INTO users (u_nome,u_email,u_idade,u_morada,u_pwd,u_sexo) VALUES ('admin','admin@email.com','18','Morada provisória','".$pwd_hashed."','1');";
                $result = mysqli_query($conn,$sql);
                if($result){
                    $sql = "INSERT INTO admin (u_id) SELECT u_id FROM users WHERE u_nome = 'admin';";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        mysqli_rollback($conn);
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error inserting in admin table";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting in users table";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }
            catch (mysqli_sql_exception $exception) {
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Exception error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            
        } 
    }
}

switch ($_POST['q']){
#021 - Adicionar um utilizador já inscrito
    case 21:
        $id1 = $_POST['name'];
        $id = $_POST['id'];

        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id1';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    mysqli_begin_transaction($conn);
                    try{
                        $sql = "INSERT INTO users (u_nome,u_morada,u_idade,u_sexo,u_email,u_pwd) SELECT tr_nome,tr_morada,tr_idade,tr_sexo,tr_email,tr_pwd FROM to_regist WHERE tr_id = '$id'";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting in users";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        $u_id = mysqli_insert_id($conn);

                        $sql ="DELETE FROM to_regist WHERE tr_id = '$id'";
                        if(!mysqli_query($conn,$sql)){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error deleting user";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $uniqid = uniqid('t_',false);
                        $sql = "INSERT INTO turmas (t_token,t_utilizada,u_id) VALUES ('$uniqid',0, (SELECT u.u_id FROM users AS u WHERE u.u_id = '$u_id' LIMIT 1) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting data in turmas";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE DATABASE ".$uniqid.";";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating database";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        mysqli_select_db($conn, $uniqid);

                        $sql = "CREATE TABLE IF NOT EXISTS educando ( e_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, e_nome VARCHAR(100) NOT NULL, e_idade INT NOT NULL, e_morada VARCHAR(200) NOT NULL, e_sexo INT NOT NULL, e_contacto VARCHAR(200) NOT NULL );";
                        $result = mysqli_query($conn, $sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table educando";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        
                        $sql = "CREATE TABLE IF NOT EXISTS atividade( a_sumario VARCHAR(300) NOT NULL, a_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, a_data VARCHAR(200) NOT NULL);";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table atividade";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS alergias( al_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, al_nome VARCHAR(100) NOT NULL);";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table alergias";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS faltas ( e_id INT NOT NULL, a_id INT NOT NULL, PRIMARY KEY (e_id, a_id), FOREIGN KEY (e_id) REFERENCES educando(e_id), FOREIGN KEY (a_id) REFERENCES atividade(a_id));";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table faltas";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS relatorio ( r_comer INT NOT NULL, r_dormir INT NOT NULL, r_coment VARCHAR(500) NOT NULL, r_necessidades INT NOT NULL, r_curativos INT NOT NULL, e_id INT NOT NULL, a_id INT NOT NULL, PRIMARY KEY (e_id, a_id), FOREIGN KEY (e_id) REFERENCES educando(e_id), FOREIGN KEY (a_id) REFERENCES atividade(a_id) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table relatorio";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS contem ( al_id INT NOT NULL, e_id INT NOT NULL, PRIMARY KEY (al_id, e_id), FOREIGN KEY (al_id) REFERENCES alergias(al_id), FOREIGN KEY (e_id) REFERENCES educando(e_id) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table contem";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        mysqli_commit($conn);
                        $responseObject->success = true;
                        $responseObject->user = $nome; 
                        $json = json_encode($responseObject);
                        echo $json;    
                        exit();  
                    }catch(mysqli_sql_exception $exception){
                        mysqli_rollback($conn);
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Exception error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Error fetching array 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Query error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }

    break;
#022 - Editar um utilizador
    case 22:
        $id = $_POST['id'];

        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    $u_id= $_POST['u_id'];
                    $username = $_POST['nome'];
                    $idade = $_POST['idade'];
                    $morada = $_POST['morada'];
                    $sexo = $_POST['sexo'];
                    $email = $_POST['email'];

                    $sql = "UPDATE users SET u_nome = '$username', u_idade = '$idade', u_morada = '$morada', u_sexo = '$sexo', u_email = '$email' WHERE u_id = '$u_id'";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Error fetching array";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Error in query";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;
#023 - Eliminar um utilizador
    case 23:
    $id = $_POST['id'];

    $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
    $result = mysqli_query($conn,$sql);
    if($result){
        if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
            if($row['c'] == 1){
                $u_id= $_POST['u_id'];
                //VERIFICAR SE O UTILIZADOR É ADMIN OU EDUCADORA, SE FOR EDUCADORA ENTÂO ELIMINAR OS DADOS DA EDUCADORA, SE FOR ADMIN ENTÂO ELIMINAR NA TABELA ADMIN

                mysqli_begin_transaction($conn);
                $sql = "SELECT count(u_id) as c FROM admin WHERE u_id = '$u_id' ";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error in query 1";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
                if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error fetching";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
                $isAdmin = $row['c'];
                if($isAdmin == 1){
                    $sql = "DELETE FROM admin WHERE u_id = '$u_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query 0";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                }else if ($isAdmin == 0){
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$u_id'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query 2";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query 6";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }

                    if($row['t_utilizada'] == 1){
                        $responseObject->warning = "A turma estava a ser utilizada";
                    }
                    $token = $row['t_token'];
                    $sql = "DROP DATABASE $token";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query 3 ".$token;
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }

                    $sql = "DELETE FROM turmas WHERE u_id = '$u_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error in query 4";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }

                }else{
                    mysqli_rollback($conn);
                    exit();
                }

                $sql = "DELETE FROM users WHERE u_id = '$u_id'";
                $result = mysqli_query($conn,$sql);
                if($result){
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    mysqli_commit($conn);
                    exit();
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error in query 5";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    mysqli_rollback($conn);
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "User not admin";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Error fetching array";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    }else{
        $responseObjectError->success = false;
        $responseObjectError->error = "Error in query";
        $json = json_encode($responseObjectError);
        echo $json;
        exit();
    }
    break;

#024 - Eliminar inscrito
    case 24:
    $id = $_POST['id'];

    $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
    $result = mysqli_query($conn,$sql);
    if($result){
        if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
            if($row['c'] == 1){
                $ins_id= $_POST['i_id'];
                $sql = "DELETE FROM to_regist WHERE tr_id = '$ins_id'";
                if(!mysqli_query($conn,$sql)){
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error in deleting";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
                mysqli_commit($conn);
                $responseObject->success = true;
                $json = json_encode($responseObject);
                echo $json;
                exit();
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "User not admin";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            } 
        } else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 2";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    } else{
        $responseObjectError->success = false;
        $responseObjectError->error = "Mysql error 1";
        $json = json_encode($responseObjectError);
        echo $json;
        exit();
    }
    break;

#050 - Registo
    case 50: //Registo
    $nome = $_POST['nome'];
    $idade = $_POST['idade'];
    $morada = $_POST['morada'];
    $sexo = $_POST['sexo'];
    $email = $_POST['email'];
    $pwd = $_POST['pass'];
    $sql = "SELECT COUNT(u_nome) as c FROM users WHERE u_nome = '$nome' ;";
    $result = mysqli_query($conn,$sql);
    if(!$result){
        $responseObjectError->success = false;
        $responseObjectError->error = "Error in query";
        $json = json_encode($responseObjectError);
        echo $json;
        exit();
    }
    if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
        if($row['c'] > 0){
            $responseObjectError->success = false;
            $responseObjectError->error = "Username taken"; //erro quando o nome de utilizador está já a ser utilizado
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    }else{
        $responseObjectError->success = false;
        $responseObjectError->error = "Error fetching array";
        $responseObjectError->debug = mysqli_error($conn);
        $json = json_encode($responseObjectError);
        echo $json;
        exit();
    }

    $sql = "INSERT INTO to_regist (tr_email,tr_sexo,tr_morada,tr_idade,tr_nome,tr_pwd) VALUES ('$email','$sexo','$morada','$idade','$nome','$pwd')";
    $result = mysqli_query($conn,$sql);
    if(!$result){
        $responseObjectError->success = false;
        $responseObjectError->error = "Data not inserted";
        $responseObjectError->debug = mysqli_error($conn);
        $json = json_encode($responseObjectError);
        echo $json;
        exit();
    }
    $responseObject->success = true;
    $json = json_encode($responseObject);
    echo $json;
    exit();

    break;
#099 - Login (administrador)
    case 99:
        $username = $_POST['u'];
        $password = $_POST['p'];
        //só faz login se não for administrador
        $sql = "SELECT u.u_id FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_nome = '$username' AND u.u_pwd = '$password'";
        $result = mysqli_query($conn,$sql);
        if($result){
            if(mysqli_num_rows($result)>0){
                if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                    $u_id = $row['u_id'];
                    $responseObject->success = true;
                    $responseObject->id = $u_id;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysqli error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;
#100 - Login (educadora)
    case 100: 
        $username = $_POST['u'];
        $password = $_POST['p'];
        //só faz login se não for administrador
        $sql = "SELECT u.u_id FROM users as u WHERE u.u_nome = '$username' AND u.u_pwd = '$password' AND NOT EXISTS ( SELECT * FROM admin a WHERE u.u_id = a.u_id ) ;";
        $result = mysqli_query($conn,$sql);
        if($result){
            if(mysqli_num_rows($result)>0){
                if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                    $u_id = $row['u_id'];
                    /* Começar uma transação para retirar todos os dados e dizer que a tabela está a ser usada */
                    mysqli_begin_transaction($conn);
                    try {                      
                        $sql = "SELECT t_token FROM turmas WHERE u_id = '$u_id';";
                        $result = mysqli_query($conn,$sql);
                        if($result){
                            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                $database_name = $row['t_token'];

                                $sql = "UPDATE turmas SET t_utilizada = 1 WHERE u_id = '$u_id';";
                                $result = mysqli_query($conn,$sql);

                                if(!$result){
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : turmas";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                mysqli_select_db($conn,$database_name);

                                $responseObject->success = true;
                                $responseObject->id = $u_id;
                                $responseObject->educando = "none";
                                $responseObject->atividade = "none";
                                $responseObject->alergias = "none";
                                $responseObject->faltas = "none";
                                $responseObject->relatorio = "none";
                                $responseObject->contem = "none";

                                $sql = "SELECT * FROM educando ORDER BY e_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->educando = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->educando .= "".$row['e_id'].",".$row['e_nome'].",".$row['e_idade'].",".$row['e_morada'].",".$row['e_sexo'].",".$row['e_contacto'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : educando";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $sql = "SELECT * FROM atividade ORDER BY a_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->atividade = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->atividade .= "".$row['a_id'].",".$row['a_sumario'].",".$row['a_data'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : atividade";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $sql = "SELECT * FROM alergias ORDER BY al_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->alergias = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->alergias .= "".$row['al_id'].",".$row['al_nome'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : alergias";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $sql = "SELECT * FROM faltas ORDER BY e_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->faltas = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->faltas .= "".$row['e_id'].",".$row['a_id'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : faltas";
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $sql = "SELECT * FROM relatorio ORDER BY e_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->relatorio = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->relatorio .= "".$row['r_comer'].",".$row['r_dormir'].",".$row['r_coment'].",".$row['r_necessidades'].",".$row['r_curativos'].",".$row['e_id'].",".$row['a_id'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : relatorio";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $sql = "SELECT * FROM contem ORDER BY e_id;";
                                $result = mysqli_query($conn,$sql);
                                if($result){
                                    $responseObject->contem = "";
                                    while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                                        $responseObject->contem .= "".$row['al_id'].",".$row['e_id'].";";
                                    }                                    
                                }else{
                                    $responseObjectError->success = false;
                                    $responseObjectError->error = "Database query error : contem";
                                    $responseObjectError->debug = mysqli_error($conn);
                                    $json = json_encode($responseObjectError);
                                    echo $json;
                                    mysqli_rollback($conn);
                                    exit();
                                }

                                $json = json_encode($responseObject);
                                echo $json;

                            }else{
                                $responseObjectError->success = false;
                                $responseObjectError->error = "Username does not contain a class";
                                $json = json_encode($responseObjectError);
                                echo $json;
                                mysqli_rollback($conn);
                                exit();
                            }
                        }else{
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Database query error";
                            $responseObjectError->debug = mysqli_error($conn);
                            $json = json_encode($responseObjectError);
                            echo $json;
                            mysqli_rollback($conn);
                            exit();
                        }
                        
                        mysqli_commit($conn);
                        exit();
                    } catch (mysqli_sql_exception $exception) {
                        mysqli_rollback($conn);
                        $responseObjectError->success = false;
                        $responseObjectError->debug = mysqli_error($conn);
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Username or password not match";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Username or password not match";
            $responseObjectError->debug = mysqli_error($conn);
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;
#101 - Adicionar um utilizador (educadora)
    case 101: 
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    mysqli_begin_transaction($conn);
                    try{
                        $nome = $_POST['un'];
                        $idade = $_POST['i'];
                        $morada = $_POST['m'];
                        $sexo = $_POST['s'];
                        $email = $_POST['e'];
                        $pwd = $_POST['pwd'];
                        $sql = "SELECT COUNT(u_nome) as c, COUNT(tr_nome) as ctr FROM users as u, to_regist as tr WHERE u_nome = '$nome' OR tr_nome = '$nome';";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error in query";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                            if($row['c'] > 0 || $row['ctr'] > 0){
                                $responseObjectError->success = false;
                                $responseObjectError->error = "Username taken"; //erro quando o nome de utilizador está já a ser utilizado
                                $json = json_encode($responseObjectError);
                                echo $json;
                                exit();
                            }
                        }else{
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error fetching array";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        $sql = "INSERT INTO users (u_nome, u_idade,u_morada,u_sexo,u_email,u_pwd) VALUES ('$nome','$idade','$morada','$sexo','$email','$pwd');";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting in users";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        $uniqid = uniqid('t_',false);
                        $sql = "INSERT INTO turmas (t_token,t_utilizada,u_id) VALUES ('$uniqid',0, (SELECT u.u_id FROM users AS u WHERE u.u_nome = '$nome' LIMIT 1) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting data in turmas";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE DATABASE ".$uniqid.";";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating database";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        mysqli_select_db($conn, $uniqid);

                        $sql = "CREATE TABLE IF NOT EXISTS educando ( e_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, e_nome VARCHAR(100) NOT NULL, e_idade INT NOT NULL, e_morada VARCHAR(200) NOT NULL, e_sexo INT NOT NULL, e_contacto VARCHAR(200) NOT NULL );";
                        $result = mysqli_query($conn, $sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table educando";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        
                        $sql = "CREATE TABLE IF NOT EXISTS atividade( a_sumario VARCHAR(300) NOT NULL, a_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, a_data VARCHAR(200) NOT NULL);";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table atividade";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS alergias( al_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, al_nome VARCHAR(100) NOT NULL);";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table alergias";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS faltas ( e_id INT NOT NULL, a_id INT NOT NULL, PRIMARY KEY (e_id, a_id), FOREIGN KEY (e_id) REFERENCES educando(e_id), FOREIGN KEY (a_id) REFERENCES atividade(a_id));";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table faltas";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS relatorio ( r_comer INT NOT NULL, r_dormir INT NOT NULL, r_coment VARCHAR(500) NOT NULL, r_necessidades INT NOT NULL, r_curativos INT NOT NULL, e_id INT NOT NULL, a_id INT NOT NULL, PRIMARY KEY (e_id, a_id), FOREIGN KEY (e_id) REFERENCES educando(e_id), FOREIGN KEY (a_id) REFERENCES atividade(a_id) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table relatorio";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        $sql = "CREATE TABLE IF NOT EXISTS contem ( al_id INT NOT NULL, e_id INT NOT NULL, PRIMARY KEY (al_id, e_id), FOREIGN KEY (al_id) REFERENCES alergias(al_id), FOREIGN KEY (e_id) REFERENCES educando(e_id) );";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error creating table contem";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }

                        mysqli_commit($conn);
                        $responseObject->success = true;
                        $responseObject->user = $nome; 
                        $json = json_encode($responseObject);
                        echo $json;    
                        exit();                   

                    }catch (mysqli_sql_exception $exception) {
                        mysqli_rollback($conn);
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Exception error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql fetch error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Database query error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;
#102 - Adicionar um utilizador (administrador)
    case 102:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    mysqli_begin_transaction($conn);
                    try{
                        $nome = $_POST['un'];
                        $idade = $_POST['i'];
                        $morada = $_POST['m'];
                        $sexo = $_POST['s'];
                        $email = $_POST['e'];
                        $pwd = $_POST['pwd'];
                        $sql = "SELECT COUNT(u.u_nome) as c, COUNT(tr.tr_nome) as ctr FROM users as u, to_regist as tr WHERE u.u_nome = '$nome' OR tr.tr_nome = '$nome';";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error in query";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                            if($row['c'] > 0 || $row['ctr']>0){
                                $responseObjectError->success = false;
                                $responseObjectError->error = "Username taken"; //erro quando o nome de utilizador está já a ser utilizado
                                $json = json_encode($responseObjectError);
                                echo $json;
                                exit();
                            }
                        }else{
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error fetching array";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        $sql = "INSERT INTO users (u_nome, u_idade,u_morada,u_sexo,u_email,u_pwd) VALUES ('$nome','$idade','$morada','$sexo','$email','$pwd');";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting in users";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        $sql = "INSERT INTO admin (SELECT u_id FROM users WHERE u_nome = '$nome' LIMIT 1);";
                        $result = mysqli_query($conn,$sql);
                        if(!$result){
                            mysqli_rollback($conn);
                            $responseObjectError->success = false;
                            $responseObjectError->error = "Error inserting in admin";
                            $json = json_encode($responseObjectError);
                            echo $json;
                            exit();
                        }
                        mysqli_commit($conn);
                        $responseObject->success = true;
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }catch(mysqli_sql_exception $exception){
                        mysqli_rollback($conn);
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Exception error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }
            }
        }
    break;

#104 - Update DB
    case 104:
        $u_name = $_POST['user'];
        $educando = $_POST['ed'];
        $atividade = $_POST['at'];
        $alergias = $_POST['al'];
        $faltas = $_POST['fa'];
        $relatorio = $_POST['rel'];
        $contem = $_POST['cont'];

        $educandolines = explode(";", $educando);
        $atividadelines = explode(";", $atividade);
        $alergiaslines = explode(";", $alergias);
        $faltaslines = explode(";", $faltas);
        $contemlines = explode(";", $contem);
        $relatoriolines = explode(";",$relatorio);

        mysqli_begin_transaction($conn);
        try{
            $sql = "SELECT t.t_token FROM users u INNER JOIN turmas t ON ( u.u_id = t.u_id ) WHERE u.u_nome = '$u_name';";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                $responseObjectError->success = false;
                $responseObjectError->error = "Error in query";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                $responseObjectError->success = false;
                $responseObjectError->error = "Error user not found ".$u_name;
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            $token = $row['t_token'];
            mysqli_select_db($conn,$token);
    

            #delete all tables 
            $sql = "DELETE FROM contem;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table contem";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            $sql = "DELETE FROM relatorio;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table relatorio";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            $sql = "DELETE FROM faltas;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table faltas";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            $sql = "DELETE FROM atividade;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table atividade";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            
            $sql = "DELETE FROM alergias;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table alergias";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }

            $sql = "DELETE FROM educando;";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Error deleting table educando";
                $responseObjectError->debug = mysqli_error($conn);
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }

            #INSERT into tables
            for($i=0;$i<count($educandolines);$i++){
                $educandocolumns = explode(",",$educandolines[$i]);
                if(empty($educandocolumns[0]))
                    continue;
                $sql = "INSERT INTO educando (e_id,e_nome,e_idade,e_morada,e_sexo,e_contacto) VALUES  ('$educandocolumns[0]','$educandocolumns[1]','$educandocolumns[2]','$educandocolumns[3]','$educandocolumns[4]','$educandocolumns[5]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into educando";
                    $responseObjectError->debug = mysqli_error($conn);
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }
    
            for($i=0;$i<count($atividadelines);$i++){
                $atividadecolumns = explode(",",$atividadelines[$i]);
                if(empty($atividadecolumns[1]))
                    continue;
                $atividadecolumns[2] = str_replace(' ', '', $atividadecolumns[2]);
                $sql = "INSERT INTO atividade (a_sumario,a_id,a_data) VALUES  ('$atividadecolumns[0]','$atividadecolumns[1]','$atividadecolumns[2]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into atividade";
                    $responseObjectError->debug = mysqli_error($conn);
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }

            for($i=0;$i<count($alergiaslines);$i++){
                $alergiascolumns = explode(",",$alergiaslines[$i]);
                if(empty($alergiascolumns[0]) || empty($alergiascolumns[1]))
                    continue;
                $sql = "INSERT INTO alergias (al_id,al_nome) VALUES  ('$alergiascolumns[0]','$alergiascolumns[1]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into alergias";
                    $responseObjectError->debug = mysqli_error($conn);
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }
    
            for($i=0;$i<count($faltaslines);$i++){
                $faltascolumns = explode(",",$faltaslines[$i]);
                if(empty($faltascolumns[0]) || empty($faltascolumns[1]) )
                    continue;
                $sql = "INSERT INTO faltas (e_id,a_id) VALUES  ('$faltascolumns[0]','$faltascolumns[1]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into faltas";
                    $responseObjectError->debug = mysqli_error($conn);
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }
    
            for($i=0;$i<count($relatoriolines);$i++){
                $relatoriocolumns = explode(",",$relatoriolines[$i]);
                if(empty($relatoriocolumns[5]) || empty($relatoriocolumns[6]))
                    continue;
                $sql = "INSERT INTO relatorio (r_comer, r_dormir, r_coment, r_necessidades, r_curativos, e_id, a_id) VALUES  ('$relatoriocolumns[0]','$relatoriocolumns[1]','$relatoriocolumns[2]','$relatoriocolumns[3]','$relatoriocolumns[4]','$relatoriocolumns[5]','$relatoriocolumns[6]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into relatorio ".$relatorio;
                    $responseObjectError->debug = mysqli_error($conn);
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }

            
            for($i=0;$i<count($contemlines);$i++){
                $contemcolumns = explode(",",$contemlines[$i]);
                if(empty($contemcolumns[0]) || empty($contemcolumns[1])){
                    continue;
                }
                $sql = "INSERT INTO contem (al_id,e_id) VALUES  ('$contemcolumns[0]','$contemcolumns[1]');";
                $result = mysqli_query($conn,$sql);
                if(!$result){
                    mysqli_rollback($conn);
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Error inserting into contem";
                    $responseObjectError->debug = "('$contemcolumns[0]','$contemcolumns[1]')";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }
    
            mysqli_commit($conn);
            $responseObject->success = true;
            $responseObject->relatorio = $relatoriolines;
            $json = json_encode($responseObject);
            echo $json;
            exit();

        }catch(mysqli_sql_exception $exception){
            mysqli_rollback($conn);
            $responseObjectError->success = false;
            $responseObjectError->error = "Exception error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;
#105 - Não utilizar mais a base de dados
    case 105:
        if ($_POST['cs'] != 1){
            $responseObjectError->success = false;
            $responseObjectError->error = "Exception error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $nome = $_POST['id'];
        $sql = "UPDATE turmas SET t_utilizada = '0' WHERE u_id = '$nome';";
        $result = mysqli_query($conn,$sql);
        if(!$result){
            $responseObjectError->success = false;
            $responseObjectError->error = "Query error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $responseObject->success = true;
        $json = json_encode($responseObject);
        echo $json;
        exit();
    break;

#106 - Enviar um email para o pai do educando 
    case 106:
        $email = $_POST['c'];
        $email = str_replace(' ','',$email);
        $u_nome = $_POST['e'];
        $data = $_POST['d'];
        $data = str_replace(' ','',$data);
        $pdf = $_FILES['f'];
        $sql = "SELECT t.t_token, u_email,u_sexo FROM users u INNER JOIN turmas t ON ( u.u_id = t.u_id ) WHERE u.u_id = '$u_nome'";
        $result = mysqli_query($conn,$sql);
        if(!$result){
            $responseObjectError->success = false;
            $responseObjectError->error = "Query error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
            $responseObjectError->success = false;
            $responseObjectError->error = "Fetching error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $database_name = $row['t_token'];
        $emaile = $row['u_email']; 
        $sexoe = $row['u_sexo'];

        if(!mysqli_select_db($conn,$row['t_token'])){
            $responseObjectError->success = false;
            $responseObjectError->error = "Error selecting database";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }

        $sql = "SELECT e.e_nome, e.e_sexo, count(*) as c FROM atividade a 
            INNER JOIN relatorio r ON ( a.a_id = r.a_id  )  
                INNER JOIN educando e ON ( r.e_id = e.e_id  )  
        WHERE a.a_data = '$data' AND
            e.e_contacto = '$email'
            GROUP BY e.e_nome, e.e_sexo";
        $result = mysqli_query($conn,$sql);
        if(!$result){
            $responseObjectError->success = false;
            $responseObjectError->error = "Query error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
            $responseObjectError->success = false;
            $responseObjectError->error = "Fetching error ".$row;
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        if(!($row['c'] > 0)){
            $responseObjectError->success = false;
            $responseObjectError->error = "Result not found";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $nome = $row['e_nome'];
        $sexo = $row['e_sexo'];

        
        //send the email
        try{
            require 'PHPMailer/src/Exception.php';
            require 'PHPMailer/src/PHPMailer.php';
            require 'PHPMailer/src/SMTP.php';

            $mail = new PHPMailer(TRUE);

            $mail->isSMTP();
            $mail->Host = $mailHost;
            $mail->SMTPAuth = "true";
            $mail->Port = $mailPort;
            $mail->Username = $mailUsername;
            $mail->Password = $mailPassword;

            $mail->CharSet = 'UTF-8';
            $mail->Encoding = 'base64';
            $mail->setFrom($mailUsername);

            /* Add a recipient. */
            $mail->addAddress($email);
            $mail->addBCC($emaile);

            /* Set the subject. */
            if($sexo){
                $mail->Subject = 'Relatório do aluno '.$nome;
            }else{
                $mail->Subject = 'Relatório da aluna '.$nome;
            }
            

            $mail->isHTML(true);
            /* Set the mail message body. */
            if($sexo){
                $mail->Body = '<html><p>Caro/a encarregado/a de educação do aluno '.$nome.',<br> Em anexo segue o relatório do dia '.$data.' em PDF. Se existir alguma dúvida poderá contactar'; 
                if($sexoe){
                    $mail->Body .= 'o educador para o email <a href="mailto:'.$emaile.'">'.$emaile.'</a>.</p></html>';
                }else{
                    $mail->Body .= 'a educadora para o email <a href="mailto:'.$emaile.'">'.$emaile.'</a>.</p></html>';
                }
            }else{
                $mail->Body = '<html><p>Caro/a encarregado/a de educação da aluna '.$nome.',<br> Em anexo segue o relatório do dia '.$data.' em PDF. Se existir alguma dúvida poderá contactar'; 
                if($sexoe){
                    $mail->Body .= 'o educador para o email <a href="mailto:'.$emaile.'">'.$emaile.'</a>.</p></html>';
                }else{
                    $mail->Body .= 'a educadora para o email <a href="mailto:'.$emaile.'">'.$emaile.'</a>.</p></html>';
                }
            }
            
            /* Set the attachment */
            if(($pdf['error'] != UPLOAD_ERR_OK)){
                $responseObjectError->success = false;
                $responseObjectError->error = "File sent error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            $dest = "/var/www/html/uploads/".$nome."_".basename($pdf['name']);
            if(!move_uploaded_file($pdf['tmp_name'],$dest)){
                $responseObjectError->success = false;
                $responseObjectError->error = "Could not upload the file";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }

            $mail->addAttachment($dest, $nome."_".basename($pdf['name']));   

            /* Finally send the mail. */
            if(!($mail->send())){
                $responseObjectError->success = false;
                $responseObjectError->error = "Mail sent error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }catch (Exception $e){
            $responseObjectError->success = false;
            $responseObjectError->error = $e->errorMessage();
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }catch (\Exception $e){
            $responseObjectError->success = false;
            $responseObjectError->error = $e->getMessage();
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $responseObject->success = true;
        $json = json_encode($responseObject);
        echo $json;
        exit();
        
    break;
#200 - Retornar a tabela dos educadores
    case 200:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    $sql = "SELECT u.u_id, u_nome, u_idade, u_morada, u_sexo, u_email, t_token, t_utilizada FROM users u INNER JOIN turmas t ON ( u.u_id = t.u_id );";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $responseObject->table = "";
                        while($row = mysqli_fetch_array($result)){
                            $responseObject->table .= $row['u_id'].",".$row['u_nome'].",".$row['u_idade'].",".$row['u_morada'].",".$row['u_sexo'].",".$row['u_email'].",".$row['t_token'].",".$row['t_utilizada'].";";
                        }
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 3";
                        $responseObjectError->debug = mysqli_errno($conn).mysqli_error($conn);
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 2";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 1";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#201 - Retornar a tabela dos administradores
    case 201:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    $sql = "SELECT u.u_id, u_nome, u_idade, u_morada, u_sexo, u_email FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id );";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $responseObject->table = "";
                        while($row = mysqli_fetch_array($result)){
                            $responseObject->table .= $row['u_id'].",".$row['u_nome'].",".$row['u_idade'].",".$row['u_morada'].",".$row['u_sexo'].",".$row['u_email'].";";
                        }
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }
            }
        }
    break;
    
#202 - Retornar a tabela dos educandos
    case 202:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $token = $_POST['t'];
                    $id_u = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE t_token = '$token' AND u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }

                    if(!mysqli_select_db($conn,$row['t_token'])){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error selecting database";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    $sql = "SELECT e_id, e_nome, e_idade, e_morada, e_sexo, e_contacto FROM educando;";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $responseObject->table = "";
                        while($row = mysqli_fetch_array($result)){
                            $responseObject->table .= $row['e_id'].",".$row['e_nome'].",".$row['e_idade'].",".$row['e_morada'].",".$row['e_sexo'].",".$row['e_contacto'].";";
                        }
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $responseObjectError->debug = mysqli_error($conn);
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }
            }
        }
    break;

#203 - Retornar as tabelas das alergias
    case 203:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $token = $_POST['t'];
                    $id_u = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE t_token = '$token' AND u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    if(!mysqli_select_db($conn,$row['t_token'])){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error selecting database";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    $sql = "SELECT a1.al_id, a1.al_nome, c.e_id
                    FROM alergias a1 
                        LEFT OUTER JOIN contem c ON ( a1.al_id = c.al_id  )  
                        GROUP BY a1.al_id, a1.al_nome, c.e_id";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $responseObject->table = "";
                        while($row = mysqli_fetch_array($result)){
                            $responseObject->table .= $row['e_id'].",".$row['al_id'].",".$row['al_nome'].";";
                        }
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }
            }
        }
    break;
#204 - Retornar tabela de atividades
    case 204:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $token = $_POST['t'];
                    $id_u = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE t_token = '$token' AND u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);
                    
                    $sql = "SELECT a_id,a_sumario,a_data FROM atividade;";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $responseObject->table = "";
                        while($row = mysqli_fetch_array($result)){
                            $responseObject->table .= $row['a_id'].",".$row['a_sumario'].",".$row['a_data'].";";
                        }
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#205 - Retornar faltas num dia
    case 205:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $token = $_POST['t'];
                    $id_u = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE t_token = '$token' AND u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    mysqli_select_db($conn,$row['t_token']);
                    $data = $_POST['d'];
                    $sql = "SELECT a_id FROM atividade WHERE a_data = '$data'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 5";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching activity";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $a_id = $row['a_id'];
                    $sql = "SELECT e.e_id, e.e_nome, a.a_id FROM atividade a INNER JOIN faltas f ON ( a.a_id = f.a_id  ) INNER JOIN educando e ON ( f.e_id = e.e_id  ) WHERE a.a_id = '$a_id';";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4 ".$a_id;
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $responseObject->success = true;
                    $responseObject->table = "";
                    while($row = mysqli_fetch_array($result)){
                        $responseObject->table .= $row['e_id'].",".$row['e_nome'].",1;";
                    }
                    $sql = "SELECT e.e_id, e.e_nome FROM educando e WHERE  NOT EXISTS ( SELECT 1 FROM faltas f1 INNER JOIN atividade a1 ON ( f1.a_id = a1.a_id  ) WHERE e.e_id = f1.e_id AND a1.a_id = '$a_id');";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 5";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    while($row = mysqli_fetch_array($result)){
                        $responseObject->table .= $row['e_id'].",".$row['e_nome'].",0;";
                    }
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                    
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#206 - retornar tabela de relatórios de uma atividade
    case 206:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $token = $_POST['t'];
                    $id_u = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE t_token = '$token' AND u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);
                    $atid = $_POST['d'];
                    $sql = "SELECT r.r_comer, r.r_dormir, r.r_coment, r.r_necessidades, r.r_curativos, e.e_nome, e.e_id
                    FROM atividade a 
                        INNER JOIN relatorio r ON ( a.a_id = r.a_id  )  
                            INNER JOIN educando e ON ( r.e_id = e.e_id  )  
                    WHERE a.a_id = '$atid' ";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = mysqli_error($conn)+"";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $responseObject->success = true;
                    $responseObject->table = "";
                    while($row = mysqli_fetch_array($result)){
                        $responseObject->table .= $row['e_id'].",".$row['e_nome'].",".$row['r_comer'].",".$row['r_dormir'].",".$row['r_coment'].",".$row['r_necessidades'].",".$row['r_curativos'].";";
                    }
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                    
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#207 - Retornar uma tabela dos inscritos
    case 207:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    $sql = "SELECT tr_id, tr_email, tr_nome FROM to_regist ";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $responseObject->success = true;
                    $responseObject->table = "";
                    while($row = mysqli_fetch_array($result)){
                        $responseObject->table .= $row['tr_id'].",".$row['tr_email'].",".$row['tr_nome'].";";
                    }
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                    
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#301 - Editar educando
    case 301:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);
                    
                    $e_id = $_POST['e_id'];
                    $e_nome = $_POST['e_nome'];
                    $e_idade = $_POST['e_idade'];
                    $e_morada = $_POST['e_morada'];
                    $e_sexo = $_POST['e_sexo'];
                    $e_contacto = $_POST['e_contacto'];
                    mysqli_begin_transaction($conn);
                    $sql = "UPDATE educando SET e_nome='$e_nome', e_idade='$e_idade', e_morada='$e_morada', e_sexo='$e_sexo',e_contacto='$e_contacto' WHERE e_id ='$e_id';";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 2";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }
            }
        }
    break;

#302 - Editar alergia
    case 302:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);
                    
                    $a_id = $_POST['a_id'];
                    $a_id = str_replace(' ', '', $a_id);
                    $a_nome = $_POST['a_nome'];
                    $sql = "UPDATE alergias SET al_nome='$a_nome' WHERE al_id ='$a_id';";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4 ('$a_id','$a_nome')".mysqli_error($conn);
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#303 - Editar atividade
    case 303:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);
                    
                    $a_id = $_POST['a_id'];
                    $a_sumario = $_POST['sum'];
                    $sql = "UPDATE atividade SET a_sumario='$a_sumario' WHERE a_id ='$a_id';";
                    $result = mysqli_query($conn,$sql);
                    if($result){
                        $responseObject->success = true;
                        $json = json_encode($responseObject);
                        echo $json;
                        exit();
                    }else{
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 4";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#304 - Editar faltas
    case 304:

        
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);

                    $tabela = $_POST['table'];
                    
                    
                    $a_id = $_POST['a_id'];
                    $a_id = str_replace(' ','',$a_id);
                    $sql = "DELETE FROM faltas WHERE a_id = '$a_id'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error ";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    if($tabela !== ''){
                        $tline = explode(",",$tabela);
                        foreach ($tline as &$line) {
                            $e_id = $line;
                            $e_id = str_replace(' ','',$e_id);
                            if(empty($e_id))
                                continue;
                            $sql = "INSERT INTO faltas (a_id,e_id) VALUES ('$a_id','$e_id')";
                            if(!mysqli_query($conn,$sql)){
                                $responseObjectError->success = false;
                                $responseObjectError->error = "Mysql error 4 ('$a_id','$e_id') - ".mysqli_error($conn);
                                $json = json_encode($responseObjectError);
                                echo $json;
                                mysqli_rollback($conn);
                                exit();
                            }
                        }
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#305 - Editar relatorios
    case 305:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $a_id = $_POST['d'];
                    $e_id = $_POST['e_id'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    $comer = $_POST['comer'];
                    $dormir = $_POST['dormir'];
                    $nec = $_POST['nec'];
                    $cur = $_POST['cur'];
                    $coment = $_POST['coment'];
                
                    $sql = "UPDATE relatorio SET r_comer = '$comer', r_dormir='$dormir', r_coment='$coment', r_necessidades='$nec', r_curativos='$cur' WHERE e_id = '$e_id' AND a_id = '$a_id'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#401 - Change password
    case 401:
        $id = $_POST['user'];
        $pwd = $_POST['pwd'];
        $pwd = str_replace(' ','',$pwd);
        $newpwd = $_POST['pwd2'];
        $newpwd = str_replace(' ','',$newpwd);
        $sql = "UPDATE users SET u_pwd = '$newpwd' WHERE (u_id = '$id' AND u_pwd = '$pwd')";
        $result = mysqli_query($conn,$sql);
        if(!$result){
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $responseObject->success = true;
        $responseObject->error = mysqli_error($conn);
        $json = json_encode($responseObject);
        echo $json;
        exit();
    break;

#402 - Forgot password (Não implementado na app mas existe esta possíbilidade no php) 
    case 402:
        //TODO tratar da parte da verificação do utilizador
        $email = $_POST['G'];
        
        $sql = "SELECT u_id, u_nome FROM users WHERE u_email='$email';";
        $result = mysqli_query($conn,$sql);
        if(!$result){
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
        $u_id = $row['u_id'];
        $u_nome = $row['u_nome'];
        $bytes = random_bytes(rand(7,21));
        $newpwd = (bin2hex($bytes))."";
        $pwd_hashed = hash('md5',$newpwd);
        mysqli_begin_transaction($conn);
        try{
            $sql = "UPDATE users SET u_pwd = '$pwd_hashed' WHERE u_id = '$u_id';";
            $result = mysqli_query($conn,$sql);
            if(!$result){
                mysqli_rollback($conn);
                $responseObjectError->success = false;
                $responseObjectError->error = "Query error";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }

            require 'PHPMailer/src/Exception.php';
            require 'PHPMailer/src/PHPMailer.php';
            require 'PHPMailer/src/SMTP.php';

            $mail = new PHPMailer(TRUE);

            $mail->isSMTP();
            $mail->Host = $mailHost;
            $mail->SMTPAuth = "true";
            $mail->Port = $mailPort;
            $mail->Username = $mailUsername;
            $mail->Password = $mailPassword;

            try {
                $mail->CharSet = 'UTF-8';
                $mail->Encoding = 'base64';
                $mail->setFrom($mailUsername);

                /* Add a recipient. */
                $mail->addAddress($email);

                /* Set the subject. */
                $mail->Subject = 'Mudança de password em Teachers Friend';

                $mail->isHTML(true);
                /* Set the mail message body. */

                $mail->Body = '<html><p>Car@ '.$u_nome.',<br> Recentemente pediu que a sua palavra passe fosse mudada no nosso sistema. A sua nova palavra passe será:</p><p style="width:100%;text-align:center;"><b>'.$newpwd.'</b></p><br><p>Recomendamos que mude a palavra passe assim que iniciar novamente a sessão.<br>Cumprimentos.</p></html>';

                /* Finally send the mail. */
                if(!($mail->send())){
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mail sent error";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
                
            }
            catch (Exception $e)
            {
                /* PHPMailer exception. */
                $responseObjectError->success = false;
                $responseObjectError->error = $e->errorMessage();
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            catch (\Exception $e)
            {
                /* PHP exception (note the backslash to select the global namespace Exception class). */
                $responseObjectError->success = false;
                $responseObjectError->error = $e->getMessage();
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
            
            $mail->smtpClose();
            mysqli_commit($conn);
            $responseObject->success = true;
            $json = json_encode($responseObject);
            echo $json;
            exit();

        }catch (mysqli_sql_exception $exception) {
            mysqli_rollback($conn);
            $responseObjectError->success = false;
            $responseObjectError->error = "Exception error";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }

    break;

#501 - Adicionar educando
    case 501:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $data = $_POST['d'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);
                    $e_nome = $_POST['e_nome'];
                    $e_idade = $_POST['e_idade'];
                    $e_morada = $_POST['e_morada'];
                    $e_sexo = $_POST['e_sexo'];
                    $e_contacto = $_POST['e_contacto'];
                    $alergias = $_POST['e_alergias'];
                    $sql = "INSERT INTO educando (e_nome,e_idade,e_morada,e_sexo,e_contacto) VALUES ('$e_nome','$e_idade','$e_morada','$e_sexo','$e_contacto')";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $e_id = mysqli_insert_id($conn);
                    $alergias = $_POST['e_alergias'];
                    $aline = explode(",",$alergias);
                    
                    if(strlen($alergias)>0){
                        foreach ($aline as &$line){
                            $line = str_replace(' ','',$line);
                            if(empty($line))
                                continue;
                            $sql = "INSERT INTO contem (e_id,al_id) VALUES ('$e_id','$line')";
                            if(!mysqli_query($conn,$sql)){
                                $responseObjectError->success = false;
                                $responseObjectError->error = "Mysql error inside foreach  $alergias";
                                $json = json_encode($responseObjectError);
                                echo $json;
                                mysqli_rollback($conn);
                                exit();
                            }
                        }
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "Mysql error 3";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#502 - Adicionar alergia
    case 502:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $data = $_POST['d'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    $a_nome = $_POST['a_nome'];
                    $sql = "INSERT INTO alergias (al_nome) VALUES ('$a_nome')";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();
                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#505 - Eliminar um educando
    case 505:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);

                    $e_id = $_POST['e_id'];
                    $sql = "DELETE FROM contem WHERE e_id = '$e_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 3";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    $sql = "DELETE FROM faltas WHERE e_id = '$e_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    $sql = "DELETE FROM relatorio WHERE e_id = '$e_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 5";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    $sql = "DELETE FROM educando WHERE e_id = '$e_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 6";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();

                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#506 - Eliminar Alergia
    case 506:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);

                    $a_id = $_POST['a_id'];
                    $a_id = str_replace(' ', '', $a_id);
                    $sql = "DELETE FROM contem WHERE al_id = '$a_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 3 ('$a_id')".mysqli_error($conn);
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    $sql = "DELETE FROM alergias WHERE al_id = '$a_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();

                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#407 - Eliminar atividade
    case 407:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);

                    $a_id = $_POST['a_id'];
                    $sql = "DELETE FROM relatorio WHERE a_id = '$a_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 3";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    $sql = "DELETE FROM atividade WHERE a_id = '$a_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 4";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        mysqli_rollback($conn);
                        exit();
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();

                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

#308 - Eliminar relatório 

    case 308:
        $id = $_POST['id'];
        $sql = "SELECT COUNT(u.u_nome) as c FROM users u INNER JOIN admin a ON ( u.u_id = a.u_id ) WHERE u.u_id = '$id';";
        $result = mysqli_query($conn,$sql);
        if($result){
            if($row = mysqli_fetch_array($result,MYSQLI_ASSOC)){
                if($row['c'] == 1){
                    //VERIFICAR SE EXISTE A BASE DE DADOS NA BASE DE DADOS MAIN E SE A MESMA ESTÁ A SER UTILIZADA NO MOMENTO
                    $id_u = $_POST['ide'];
                    $sql = "SELECT t_utilizada,t_token FROM turmas WHERE u_id = '$id_u'";
                    $result = mysqli_query($conn,$sql);
                    if(!$result){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error in turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if(!($row = mysqli_fetch_array($result,MYSQLI_ASSOC))){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Error fetching turmas";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    if($row['t_utilizada'] != 0){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Turma a ser utilizada";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    mysqli_select_db($conn,$row['t_token']);

                    mysqli_begin_transaction($conn);

                    $a_id = $_POST['a_id'];
                    $e_id = $_POST['e_id'];
                    $sql = "DELETE FROM relatorio WHERE a_id = '$a_id' AND e_id = '$e_id'";
                    if(!mysqli_query($conn,$sql)){
                        $responseObjectError->success = false;
                        $responseObjectError->error = "Mysql error 3";
                        $json = json_encode($responseObjectError);
                        echo $json;
                        exit();
                    }
                    
                    mysqli_commit($conn);
                    $responseObject->success = true;
                    $json = json_encode($responseObject);
                    echo $json;
                    exit();

                }else{
                    $responseObjectError->success = false;
                    $responseObjectError->error = "User not admin";
                    $json = json_encode($responseObjectError);
                    echo $json;
                    exit();
                }
            }else{
                $responseObjectError->success = false;
                $responseObjectError->error = "Mysql error 2";
                $json = json_encode($responseObjectError);
                echo $json;
                exit();
            }
        }else{
            $responseObjectError->success = false;
            $responseObjectError->error = "Mysql error 1";
            $json = json_encode($responseObjectError);
            echo $json;
            exit();
        }
    break;

# default - sair da página
    default:
        exit();
    break;

}
