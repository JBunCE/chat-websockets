import React, { useEffect, useState } from 'react'
import {over} from 'stompjs';
import SockJS from 'sockjs-client';
import Swal from 'sweetalert2';

var stompClient =null;
const ChatRoom = () => {

    const URI = "http://localhost:8080"
    const [privateChats, setPrivateChats] = useState(new Map());     
    const [publicChats, setPublicChats] = useState([]); 
    const [tab,setTab] =useState("CHATROOM");
    const [userData, setUserData] = useState({
        username: '',
        password: '',
        receivername: '',
        connected: false,
        loginStatus: false,
        logInApttemt: false,
        message: '',
        file: null,
        fileName: ''
    });
    const [userReg, setUserReg] = useState({
        username: '',
        password: '',
        pivotPassword: ''
    })
    const [awsFile, setAwsFile] = useState(null);


    useEffect(() => {
      console.log(userData);
    }, [userData]);

    const connect =()=>{
        let Sock = new SockJS(URI +'/ws');
        stompClient = over(Sock);
        stompClient.connect({},onConnected, onError);
        userJoin();
    }

    const onConnected = () => {
        setUserData({...userData,"connected": true});
        stompClient.subscribe('/chatroom/public', onMessageReceived);
        stompClient.subscribe('/user/'+userData.username+'/private', onPrivateMessage);
        userJoin();
    }

    const userJoin=()=>{
          let chatMessage = {
            senderName: userData.username,
            status:"JOIN"
          };
          stompClient.send("/app/messageInput", {}, JSON.stringify(chatMessage));
    }

    const onMessageReceived = (payload)=>{
        console.log(payload);
        var payloadData = JSON.parse(payload.body);
        switch(payloadData.status){
            case "JOIN":
                if(!privateChats.get(payloadData.senderName)){
                    privateChats.set(payloadData.senderName,[]);
                    setPrivateChats(new Map(privateChats));
                    userJoin();
                }
                break;
            case "MESSAGE":
                publicChats.push(payloadData);
                setPublicChats([...publicChats]);
                break;
        }
    }
    
    const onPrivateMessage = (payload)=>{
        console.log(payload);
        var payloadData = JSON.parse(payload.body);
        if(privateChats.get(payloadData.senderName)){
            privateChats.get(payloadData.senderName).push(payloadData);
            setPrivateChats(new Map(privateChats));
        }else{
            let list =[];
            list.push(payloadData);
            privateChats.set(payloadData.senderName,list);
            setPrivateChats(new Map(privateChats));
        }
    }

    const onError = (err) => {
        console.log(err);
        
    }

    const sendValue=()=>{
        console.log(stompClient);
            if (stompClient) {
              let chatMessage = {
                senderName: userData.username,
                content: userData.message,
                file: userData.file,
                fileName: userData.fileName,
                status:"MESSAGE"
              };
              console.log(chatMessage);
              stompClient.send("/app/messageInput", {}, JSON.stringify(chatMessage));
              setUserData({...userData,"message": "", "file" : null});
            }
    }

    const sendPrivateValue=()=>{
        if (stompClient) {
          let chatMessage = {
            senderName: userData.username,
            receiverName:tab,
            content: userData.message,
            file: userData.file,
            fileName: userData.fileName,
            status:"MESSAGE"
          };
          let fileName = userData.fileName.replace(/\s+/g, '-');
          let chatMessageLocal = {
            senderName: userData.username,
            receiverName:tab,
            content: userData.message,
            fileUrl: (userData.file !== null) ? "https://conejochatbucket.s3.us-east-1.amazonaws.com/files/" + fileName : null,
            fileName: userData.fileName,
            status:"MESSAGE"
          };
          if(userData.username !== tab){
            privateChats.get(tab).push(chatMessageLocal);
            setPrivateChats(new Map(privateChats));
          }
          stompClient.send("/app/private-messageInput", {}, JSON.stringify(chatMessage));
          setUserData({...userData,"message": "", "file" : null});
          
        }
    }

    const handleMessage =(event)=>{
        const {value}=event.target;
        setUserData({...userData,"message": value});
    }

    const handleUsername=(event)=>{
        const {value}=event.target;
        setUserData({...userData,"username": value});
    }

    const handlePassword=(event)=>{
        const {value}=event.target;
        setUserData({...userData,"password": value});
    }

    const handleRegUserName = (event) =>{
        const {value}=event.target;
        setUserReg({...userReg,"username": value});
    }

    const handleRegUserPassword = (event) =>{
        const {value}=event.target;
        setUserReg({...userReg,"password": value});
    }

    const handleFileChange = (event) =>{
        const fileread = event.target.files[0];
        const reader = new FileReader();
       
        console.log(fileread);
        reader.onload = () =>{
            const base64String = reader.result.replace('data:', '').replace(/^.+,/, '');
            console.log(base64String);
            setUserData({...userData, 
                'file': base64String,
                'fileName': fileread.name })
        }
        reader.readAsDataURL(fileread);
    }

    const registerUser=()=>{

        fetch(URI + "/user/reg", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: userReg.username,
                password: userReg.password
            })
        }).then(response => response.json())
        .then(response =>{
            console.log(response)
            if(response.success === true){
                Swal.fire(
                    'registro exitoso',
                    'success'
                )
            }else {
                Swal.fire({
                    icon: 'error',
                    title: response.message,
                    text: 'porfavor introduzca credenciales diferente',
                })
            }

        });
    }

    const signUser = () =>{
        fetch(URI + "/user/login", {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                name: userData.username,
                password: userData.password
            })
        }).then(response => response.json())
        .then(response => {
            if(response.success === true){
                connect();
            } else {
                Swal.fire({
                    icon: 'error',
                    title: response.message,
                    text: 'porfavor introduzca credenciales diferente',
                })
            }

        })

    }

    return (
    <div className="container">
        {userData.connected?
        <div className="chat-box">
            <div className="member-list">
                <ul>
                    <li onClick={()=>{setTab("CHATROOM")}} className={`member ${tab==="CHATROOM" && "active"}`}>Chatroom</li>
                    {[...privateChats.keys()].map((name,index)=>(
                        userData.username !== name && 
                        <li onClick={()=>{setTab(name)}} className={`member ${tab===name && "active"}`} key={index}>{name}</li>     
                    ))}
                </ul>
            </div>
            {tab==="CHATROOM" && <div className="chat-content">
                <ul className="chat-messages">
                    {publicChats.map((chat,index)=>(
                        <li className={`message ${chat.senderName === userData.username && "self"}`} key={index}>
                            {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                            <div className="message-data">{chat.content}</div>
                            {chat.fileUrl !== null && <a type='link' className='avatar file' target="_blank" href={chat.fileUrl}> archivo </a>}
                            {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                        </li>
                    ))}
                </ul>
                <div className='send-message'>
                    <input type="file" id="inputArchivo" placeholder='select file'  onChange={handleFileChange} />
                    <label htmlFor="inputArchivo">{userData.file ? " Archivo seleccioando " : "Seleccionar archivo"}</label>
                </div>
                <div className="send-message">
                    <input type="text" className="input-message" placeholder="enter the message" value={userData.message} onChange={handleMessage} /> 
                    <button type="button" className="send-button" onClick={sendValue}>send</button>
                </div>
            </div>}
            {tab!=="CHATROOM" && <div className="chat-content">
                <ul className="chat-messages">
                    {[...privateChats.get(tab)].map((chat,index)=>(
                        <li className={`message ${chat.senderName === userData.username && "self"}`} key={index}>
                            {chat.senderName !== userData.username && <div className="avatar">{chat.senderName}</div>}
                            <div className="message-data">{chat.content}</div>
                            {(chat.fileUrl !== null) && <a type='link' className='avatar file' target="_blank" href={chat.fileUrl}> archivo </a>}
                            {chat.senderName === userData.username && <div className="avatar self">{chat.senderName}</div>}
                        </li>
                    ))}
                </ul>
                <div className='send-message'>
                    <input type="file" id="inputArchivo" placeholder='select file'  onChange={handleFileChange} />
                    <label htmlFor="inputArchivo">{userData.file ? " Archivo seleccioando " : "Seleccionar archivo"}</label>
                </div>
                <div className="send-message">
                    <input type="text" className="input-message" placeholder="enter the message" value={userData.message} onChange={handleMessage} /> 
                    <button type="button" className="send-button" onClick={sendPrivateValue}>send</button>
                </div>
            </div>}
        </div>
        :
        <div className="regCont">
            <div className="signin">

                <h2>Iniciar sesion</h2>
                
                <input
                    id="user-name"
                    placeholder="Enter your name"
                    name="userName"
                    value={userData.username}
                    onChange={handleUsername}
                    margin="normal"
                />

                <input
                    type="password"
                    id="user-name"
                    placeholder="Enter your password"
                    name="userName"
                    value={userData.password}
                    onChange={handlePassword}
                    margin="normal"
                />
                <button type="button" onClick={signUser}>
                    connect
                </button> 
            </div>
            <div className='signup'>
            <h2>Registrarse</h2>
            <input
                id="user-name"
                placeholder="Enter your user name"
                name="userNameReg"
                value={userReg.username}
                onChange={handleRegUserName}
                margin="normal"
            />
            <input
                type="password"
                id="user-name"
                placeholder="repeat your password"
                name="userName"
                value={userReg.password}
                onChange={handleRegUserPassword}
                margin="normal"
            />
                <button type="button" onClick={registerUser}>
                    register
                </button> 
            </div> 
        </div>
        }
    </div>
    )
}

export default ChatRoom