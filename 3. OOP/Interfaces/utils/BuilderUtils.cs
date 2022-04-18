using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Interfaces.utils
{
    internal static class BuilderUtils
    {
        public static bool isCar(Type type)
        {
            if(type.GetMethod("GetFrame")!=null)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
