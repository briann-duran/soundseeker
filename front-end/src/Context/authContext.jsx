import { createContext, useContext, useState } from 'react';
import { toast, Toaster } from 'sonner';

export const AuthContext = createContext();

export const AuthContextProvider = ({ children }) => {
  const [user, setUser] = useState();
  const [isLoading, setIsLoading] = useState(false);

  const login = async (settings) => {
    try {
      const response = await fetch(
        `${import.meta.env.VITE_APP_BACK_END_URL}/autenticacion`,
        settings
      );
      if (response.ok) {
        const data = await response.json();
        const newUser = {
          nombreUsuario: data.nombreUsuario,
          nombre: data.nombre,
          apellido: data.apellido,
          correoElectronico: data.correoElectronico,
          roles: data.roles,
        };

        localStorage.setItem('user', JSON.stringify(newUser));

        const token = response.headers.get('Authorization');
        localStorage.setItem('jwt', token);
        location.replace('/home');
      } else {
        const errorMessage = await response.json();
        toast.error(errorMessage.message);
      }
    } catch (error) {
      alert(error.message.message);
    }
  };

  const signUp = (settings) => {
    setIsLoading(true);
    const tiempoMaximo = 60 * 1000;
    const inicioTiempo = new Date().getTime();

    const data = JSON.parse(settings.body);

    const formData = {
      nombreUsuario: data.correoElectronico,
      contrasena: data.contrasena,
    };

    const settings2 = {
      method: 'POST',
      headers: {
        'Content-type': 'application/json; charset=UTF-8',
      },
      body: JSON.stringify(formData),
    };

    const realizarSegundaSolicitud = async () => {
      try {
        const response = await fetch(
          `${import.meta.env.VITE_APP_BACK_END_URL}/autenticacion`,
          settings2
        );

        if (response.ok) {
          const resJSON = await response.json();

          const newUser = {
            nombreUsuario: resJSON.nombreUsuario,
            nombre: resJSON.nombre,
            apellido: resJSON.apellido,
            correoElectronico: resJSON.correoElectronico,
            roles: resJSON.roles,
          };

          localStorage.setItem('user', JSON.stringify(newUser));
          const token = response.headers.get('Authorization');
          localStorage.setItem('jwt', token);
          location.replace('/home');
        } else {
          throw new Error(
            'La solicitud de inicio de sesión no se completó correctamente.'
          );
        }
      } catch (err) {
        const tiempoActual = new Date().getTime();
        if (tiempoActual - inicioTiempo < tiempoMaximo) {
          setTimeout(realizarSegundaSolicitud, 1000);
        } else {
          setIsLoading(false);
          toast.error('Se ha superado el tiempo máximo de espera.');
        }
      }
    };

    fetch(`${import.meta.env.VITE_APP_BACK_END_URL}/usuarios`, settings)
      .then((response) => {
        if (response.ok) {
          realizarSegundaSolicitud().then();
        } else {
          setIsLoading(false);
          toast.error('La solicitud no se completó correctamente.');
        }
      })
      .catch((err) => {
        toast.error(err.message);
        setIsLoading(false);
      });
  };

  const logout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('jwt');
    setUser(null);
    window.location.reload();
  };

  return (
    <AuthContext.Provider
      value={{ user, login, signUp, logout, isLoading, setIsLoading }}
    >
      <Toaster position="top-right" richColors />
      {children}
    </AuthContext.Provider>
  );
};

export const useRecipeAuth = () => useContext(AuthContext);
